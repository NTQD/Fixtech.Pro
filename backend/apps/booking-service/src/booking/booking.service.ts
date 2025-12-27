import { Injectable, BadRequestException, NotFoundException } from '@nestjs/common';
import { DataSource } from 'typeorm';
import { Booking } from './entities/booking.entity';
import { BookingStatus } from './entities/booking-status.enum';
import { BookingItem } from './entities/booking-item.entity';
import { BookingHistory } from './entities/booking-history.entity';
import { BookingReview } from './entities/booking-review.entity';
import { HttpService } from '@nestjs/axios';
import { lastValueFrom, catchError, of } from 'rxjs';
import { ConfigService } from '@nestjs/config';
import { parseDuration } from './utils/time.utils'; // Added import

@Injectable()
export class BookingService {
    private authServiceUrl: string;
    private catalogServiceUrl: string;

    constructor(
        private dataSource: DataSource,
        private httpService: HttpService,
        private configService: ConfigService
    ) {
        this.authServiceUrl = this.configService.get<string>('AUTH_SERVICE_URL') || 'http://localhost:3002';
        this.catalogServiceUrl = this.configService.get<string>('CATALOG_SERVICE_URL') || 'http://localhost:3003';
    }

    private async getPart(partId: number): Promise<any> {
        try {
            const url = `${this.catalogServiceUrl}/catalog/parts/${partId}`;
            const response = await lastValueFrom(this.httpService.get(url));
            return response.data;
        } catch (e) {
            console.error(`Failed to fetch part ${partId} from ${this.catalogServiceUrl}/catalog/parts/${partId}`, e.message, e.response?.data);
            return null;
        }
    }

    private async getService(serviceId: string): Promise<any> {
        try {
            const url = `${this.catalogServiceUrl}/catalog/services/${serviceId}`;
            const response = await lastValueFrom(this.httpService.get(url));
            return response.data;
        } catch (e) {
            console.error(`Failed to fetch service ${serviceId} from ${this.catalogServiceUrl}/catalog/services/${serviceId}`, e.message, e.response?.data);
            return null;
        }
    }

    async createBooking(createBookingDto: any, userId: number): Promise<Booking> {
        const { items, ...bookingData } = createBookingDto;

        const queryRunner = this.dataSource.createQueryRunner();
        await queryRunner.connect();
        await queryRunner.startTransaction();

        try {
            const booking = queryRunner.manager.create(Booking, {
                ...bookingData,
                user_id: userId,
                status: BookingStatus.PENDING,
                total_amount: 0,
            });

            let assignedTechnicianId = null;

            // --- SMART ASSIGNMENT LOGIC ---
            try {
                // 1. Calculate Total Duration
                let totalDurationMinutes = 0;
                if (items && items.length > 0) {
                    for (const item of items) {
                        if (item.service_id) {
                            const service = await this.getService(item.service_id);
                            if (service) {
                                totalDurationMinutes += parseDuration(service.estimated_duration);
                            } else {
                                totalDurationMinutes += 60; // Default buffer
                            }
                        }
                    }
                }
                if (totalDurationMinutes === 0) totalDurationMinutes = 60; // Default if no services

                // 2. Find Available Technician
                assignedTechnicianId = await this.checkTechnicianAvailability(
                    bookingData.scheduled_date,
                    bookingData.scheduled_time,
                    totalDurationMinutes
                );

                if (!assignedTechnicianId) {
                    throw new BadRequestException('Tất cả kỹ thuật viên đều bận vào khung giờ này. Vui lòng chọn giờ khác.');
                }
                booking.technician_id = assignedTechnicianId;

            } catch (e) {
                // If the error is our specific "Busy" error, we must re-throw it to stop the booking
                if (e instanceof BadRequestException) {
                    throw e;
                }
                console.log('Failed to check availability or fetch technicians, falling back to random (unsafe)', e.message);
            }
            // -----------------------------

            await queryRunner.manager.save(booking);

            // Record Initial History
            const history = queryRunner.manager.create(BookingHistory, {
                booking,
                booking_id: booking.id,
                status: BookingStatus.PENDING,
                note: 'Order created'
            });
            await queryRunner.manager.save(history);

            let totalAmount = 0;

            if (items && items.length > 0) {
                for (const item of items) {
                    let price = 0;

                    if (item.part_id) {
                        const part = await this.getPart(item.part_id);
                        if (!part) throw new NotFoundException(`Part ${item.part_id} not found`);
                        if (part.stock < item.quantity) {
                            throw new BadRequestException(`Insufficient stock for part: ${part.name}`);
                        }
                        price = Number(part.price);
                    } else if (item.service_id) {
                        const service = await this.getService(item.service_id);
                        if (!service) throw new NotFoundException(`Service ${item.service_id} not found`);
                        price = Number(service.base_price);
                    }

                    const bookingItem = queryRunner.manager.create(BookingItem, {
                        booking,
                        booking_id: booking.id,
                        service_id: item.service_id,
                        part_id: item.part_id,
                        quantity: item.quantity,
                        price: price,
                    });
                    await queryRunner.manager.save(bookingItem);
                    totalAmount += price * item.quantity;
                }
            }

            booking.total_amount = totalAmount;
            await queryRunner.manager.save(booking);

            await queryRunner.commitTransaction();
            return booking;

        } catch (err) {
            await queryRunner.rollbackTransaction();
            throw err;
        } finally {
            await queryRunner.release();
        }
    }

    async findAll(currentUser: any) {
        const whereCondition: any = {};

        // Access Control
        if (currentUser?.role === 'TECHNICIAN') {
            whereCondition.technician_id = currentUser.userId;
        }
        if (currentUser?.role === 'CUSTOMER') {
            whereCondition.user_id = currentUser.userId;
        }

        const bookings = await this.dataSource.getRepository(Booking).find({
            where: whereCondition,
            relations: ['items', 'history'],
            order: { created_at: 'DESC' }
        });

        return this.enrichBookings(bookings);
    }

    async findMyBookings(user: any) {
        // STRICTLY return bookings created by this user ID, ignoring their Admin/Tech role.
        // This is for the "Profile" page where they view their own personal requests.
        const bookings = await this.dataSource.getRepository(Booking).find({
            where: { user_id: user.userId },
            relations: ['items', 'history'],
            order: { created_at: 'DESC' }
        });
        return this.enrichBookings(bookings);
    }

    async updateStatus(id: number, status: string) {
        if (!Object.values(BookingStatus).includes(status as BookingStatus)) {
            throw new BadRequestException(`Invalid status`);
        }
        const booking = await this.dataSource.getRepository(Booking).findOne({ where: { id } });
        if (!booking) throw new NotFoundException(`Booking not found`);

        booking.status = status as BookingStatus;
        const savedBooking = await this.dataSource.getRepository(Booking).save(booking);

        // Record History
        const history = this.dataSource.getRepository(BookingHistory).create({
            booking,
            booking_id: booking.id,
            status: status as BookingStatus,
            note: `Status updated to ${status}`
        });
        await this.dataSource.getRepository(BookingHistory).save(history);

        return savedBooking;
    }

    async assignTechnician(bookingId: number, technicianId: number) {
        const booking = await this.dataSource.getRepository(Booking).findOne({ where: { id: bookingId } });
        if (!booking) throw new NotFoundException(`Booking found`);

        try {
            await lastValueFrom(this.httpService.get(`${this.authServiceUrl}/users/${technicianId}`));
        } catch {
            throw new NotFoundException(`Technician not found`);
        }

        booking.technician_id = technicianId;
        return this.dataSource.getRepository(Booking).save(booking);
    }

    async addPartToBooking(bookingId: number, partId: number, quantity: number) {
        const booking = await this.dataSource.getRepository(Booking).findOne({ where: { id: bookingId } });
        if (!booking) throw new NotFoundException(`Booking not found`);

        const part = await this.getPart(Number(partId));
        if (!part) throw new NotFoundException(`Part not found (ID: ${partId})`);

        const bookingItem = this.dataSource.getRepository(BookingItem).create({
            booking,
            booking_id: booking.id,
            part_id: part.id,
            quantity: quantity,
            price: Number(part.price),
        });
        await this.dataSource.getRepository(BookingItem).save(bookingItem);

        // Update Total
        await this.recalculateBookingTotal(booking.id);

        return booking;
    }

    async addServiceToBooking(bookingId: number, serviceId: string) {
        const booking = await this.dataSource.getRepository(Booking).findOne({ where: { id: bookingId } });
        if (!booking) throw new NotFoundException(`Booking not found`);

        const service = await this.getService(serviceId);
        if (!service) throw new NotFoundException(`Service not found (ID: ${serviceId})`);

        const bookingItem = this.dataSource.getRepository(BookingItem).create({
            booking,
            booking_id: booking.id,
            service_id: service.id,
            quantity: 1,
            price: Number(service.base_price),
        });
        await this.dataSource.getRepository(BookingItem).save(bookingItem);

        // Update Total
        await this.recalculateBookingTotal(booking.id);

        return booking;
    }

    async recalculateBookingTotal(bookingId: number) {
        const booking = await this.dataSource.getRepository(Booking).findOne({
            where: { id: bookingId },
            relations: ['items']
        });
        if (!booking) return;

        const total = booking.items.reduce((sum, item) => {
            return sum + (Number(item.price) * Number(item.quantity));
        }, 0);

        booking.total_amount = total;
        await this.dataSource.getRepository(Booking).save(booking);
    }

    // --- AVAILABILITY LOGIC ---
    async checkTechnicianAvailability(date: Date, time: string, durationMinutes: number): Promise<number | null> {
        // 1. Fetch All Technicians
        let technicians: any[] = [];
        try {
            const techResponse: any = await lastValueFrom(this.httpService.get(`${this.authServiceUrl}/users?role=TECHNICIAN`));
            technicians = techResponse.data;
        } catch (e) {
            console.error("Failed to fetch technicians for availability check", e);
            return null;
        }

        if (!technicians || technicians.length === 0) return null;

        // 2. Calculate Request Time Window
        // date is likely a string or Date object. We need to combine it with time "HH:mm:ss"
        const reqStart = new Date(`${date}T${time}`);
        // If date format is strict, ensure it matches. Assuming YYYY-MM-DD from frontend.
        const reqEnd = new Date(reqStart.getTime() + durationMinutes * 60000);

        // 3. Check Each Technician
        for (const tech of technicians) {
            const isBusy = await this.isTechnicianBusy(tech.id, date, reqStart, reqEnd);
            if (!isBusy) {
                return tech.id; // Found a free tech!
            }
        }

        return null; // All busy
    }

    private async isTechnicianBusy(techId: number, dateStr: any, reqStart: Date, reqEnd: Date): Promise<boolean> {
        // Find existing bookings for this tech on this date
        // Note: In TypeORM, querying date strings can be database specific. 
        // We'll fetch all bookings for this tech on this day.
        const bookings = await this.dataSource.getRepository(Booking).createQueryBuilder('booking')
            .leftJoinAndSelect('booking.items', 'items')
            .where('booking.technician_id = :techId', { techId })
            .andWhere('booking.scheduled_date = :dateStr', { dateStr })
            .andWhere('booking.status IN (:...statuses)', { statuses: [BookingStatus.PENDING, BookingStatus.IN_PROGRESS] })
            .getMany();

        for (const booking of bookings) {
            // Calculate duration of THIS booking
            // We need to fetch services to know duration? 
            // This is expensive. Optimization: Store 'estimated_end_time' in Booking entity in future.
            // For now, we fetch catalog service for each item? 
            // To ensure performance, let's assume a default or fetch. 
            // Better: We stored `service_id` in items.

            let bookingDuration = 0;
            for (const item of booking.items) {
                if (item.service_id) {
                    const service = await this.getService(item.service_id); // Cached or fast fetch
                    bookingDuration += parseDuration(service?.estimated_duration);
                }
            }
            if (bookingDuration === 0) bookingDuration = 60;

            const existingStart = new Date(`${booking.scheduled_date}T${booking.scheduled_time}`);
            const existingEnd = new Date(existingStart.getTime() + bookingDuration * 60000);

            // Check Overlap
            // (StartA < EndB) and (EndA > StartB)
            if (reqStart < existingEnd && reqEnd > existingStart) {
                return true; // Overlap found, tech is busy
            }
        }

        return false;
    }

    async search(query: string, user: any) {
        if (!query) return [];

        const qb = this.dataSource.getRepository(Booking).createQueryBuilder('booking')
            .leftJoinAndSelect('booking.items', 'items')
            .leftJoinAndSelect('booking.history', 'history')
            .orderBy('booking.created_at', 'DESC');

        if (!isNaN(Number(query))) {
            const isShortQuery = query.length < 6; // Avoid broad phone matches for short numbers
            if (isShortQuery) {
                // Strict ID search only for short numbers
                qb.where('booking.id = :id', { id: Number(query) });
            } else {
                // ID OR Phone for longer numbers
                qb.where(
                    '(booking.id = :id OR booking.customer_phone LIKE :phone)',
                    { id: Number(query), phone: `%${query}%` }
                );
            }
        } else {
            qb.where('booking.customer_phone LIKE :phone', { phone: `%${query}%` });
        }

        // Restrict access for non-admin/tech users
        const role = user.role.toLowerCase();
        if (role !== 'admin' && !role.includes('tech')) {
            // For customers, ONLY show their own bookings.
            // We can match by user_id
            qb.andWhere('booking.user_id = :userId', { userId: user.userId });
        }

        const bookings = await qb.getMany();
        return this.enrichBookings(bookings);
    }

    async cancelBooking(id: number, currentUser: any) {
        const booking = await this.dataSource.getRepository(Booking).findOne({ where: { id } });
        if (!booking) throw new NotFoundException(`Booking not found`);

        // Removed strict ownership/phone check as requested for simpler UX.
        // We rely on the fact that the user must know the Booking ID to even see this button.

        // Status Check
        if ([BookingStatus.IN_PROGRESS, BookingStatus.COMPLETED, BookingStatus.CANCELLED].includes(booking.status)) {
            throw new BadRequestException(`Cannot cancel booking with status ${booking.status}`);
        }

        booking.status = BookingStatus.CANCELLED;
        const savedBooking = await this.dataSource.getRepository(Booking).save(booking);

        // Record History
        const history = this.dataSource.getRepository(BookingHistory).create({
            booking,
            booking_id: booking.id,
            status: BookingStatus.CANCELLED,
            note: currentUser ? `Cancelled by user ${currentUser.username || currentUser.userId}` : `Cancelled by customer`
        });
        await this.dataSource.getRepository(BookingHistory).save(history);

        return savedBooking;
    }

    async rateBooking(bookingId: number, ratingDto: { technician_rating: number, comment: string }, user: any) {
        const booking = await this.dataSource.getRepository(Booking).findOne({ where: { id: bookingId } });
        if (!booking) throw new NotFoundException(`Booking not found`);

        if (booking.user_id !== user.userId) {
            throw new BadRequestException(`You can only rate your own bookings`);
        }

        if (booking.status !== BookingStatus.COMPLETED) {
            throw new BadRequestException(`Only completed bookings can be rated`);
        }

        if (!booking.technician_id) {
            // Technically shouldn't happen for completed bookings, but safety check
            throw new BadRequestException(`No technician assigned to this booking`);
        }

        // Check availability using BookingReview entity
        // I need to make sure BookingReview is imported. I will add it to the top imports in a separate check or assume it's added.
        // Actually, let's use the string name 'BookingReview' or import it.
        // I will add the import in a separate block to be safe.
        const existingReview = await this.dataSource.getRepository(BookingReview).findOne({ where: { booking_id: bookingId } });
        if (existingReview) {
            throw new BadRequestException(`This booking has already been rated`);
        }

        const review = this.dataSource.getRepository(BookingReview).create({
            booking_id: bookingId,
            technician_rating: ratingDto.technician_rating,
            comment: ratingDto.comment
        });

        await this.dataSource.getRepository(BookingReview).save(review);

        // Update Technician Reputation
        try {
            await lastValueFrom(this.httpService.patch(`${this.authServiceUrl}/users/${booking.technician_id}/reputation`, {
                rating: ratingDto.technician_rating
            }));
        } catch (e) {
            console.error(`Failed to update reputation for technician ${booking.technician_id}`, e.message);
            // We don't rollback the review because the rating is valid, just the sync failed. 
            // We could re-sync later or log it.
        }

        return review;
    }

    private async enrichBookings(bookings: Booking[]) {
        return Promise.all(bookings.map(async (booking) => {
            let user = null;
            let technician = null;
            let review = null;

            try {
                const userResponse: any = await lastValueFrom(
                    this.httpService.get(`${this.authServiceUrl}/users/${booking.user_id}`).pipe(
                        catchError(() => of({ data: null }))
                    )
                );
                user = userResponse.data;
            } catch (e) { }

            if (booking.technician_id) {
                try {
                    const techResponse: any = await lastValueFrom(
                        this.httpService.get(`${this.authServiceUrl}/users/${booking.technician_id}`).pipe(
                            catchError(() => of({ data: null }))
                        )
                    );
                    technician = techResponse.data;
                } catch (e) { }
            }

            try {
                review = await this.dataSource.getRepository(BookingReview).findOne({ where: { booking_id: booking.id } });
            } catch (e) { }

            const items = await Promise.all(booking.items.map(async (item) => {
                let part = null;
                let service = null;
                if (item.part_id) {
                    try {
                        const pRes: any = await lastValueFrom(
                            this.httpService.get(`${this.catalogServiceUrl}/catalog/parts/${item.part_id}`).pipe(
                                catchError(() => of({ data: null }))
                            )
                        );
                        part = pRes.data;
                    } catch (e) { }
                }
                if (item.service_id) {
                    try {
                        const sRes: any = await lastValueFrom(
                            this.httpService.get(`${this.catalogServiceUrl}/catalog/services/${item.service_id}`).pipe(
                                catchError(() => of({ data: null }))
                            )
                        );
                        service = sRes.data;
                    } catch (e) { }
                }
                return { ...item, part, service };
            }));

            return { ...booking, user, technician, items, is_rated: !!review, review };
        }));
    }
}
