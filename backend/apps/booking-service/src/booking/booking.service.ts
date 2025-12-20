import { Injectable, BadRequestException, NotFoundException } from '@nestjs/common';
import { DataSource } from 'typeorm';
import { Booking } from './entities/booking.entity';
import { BookingStatus } from './entities/booking-status.enum';
import { BookingItem } from './entities/booking-item.entity';
import { BookingHistory } from './entities/booking-history.entity';
import { HttpService } from '@nestjs/axios';
import { lastValueFrom, catchError, of } from 'rxjs';
import { ConfigService } from '@nestjs/config';

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

            try {
                const techResponse: any = await lastValueFrom(this.httpService.get(`${this.authServiceUrl}/users?role=TECHNICIAN`));
                const technicians = techResponse.data;
                if (technicians && technicians.length > 0) {
                    const randomTech = technicians[Math.floor(Math.random() * technicians.length)];
                    booking.technician_id = randomTech.id;
                }
            } catch (e) {
                console.log('Failed to fetch technicians', e.message);
            }

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
        return this.findAll(user);
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

    private async recalculateBookingTotal(bookingId: number) {
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

    async search(query: string) {
        if (!query) return [];

        const qb = this.dataSource.getRepository(Booking).createQueryBuilder('booking')
            .leftJoinAndSelect('booking.items', 'items')
            .leftJoinAndSelect('booking.history', 'history')
            .orderBy('booking.created_at', 'DESC');

        if (!isNaN(Number(query))) {
            qb.where(
                '(booking.id = :id OR booking.customer_phone LIKE :phone)',
                { id: Number(query), phone: `%${query}%` }
            );
        } else {
            qb.where('booking.customer_phone LIKE :phone', { phone: `%${query}%` });
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

    private async enrichBookings(bookings: Booking[]) {
        return Promise.all(bookings.map(async (booking) => {
            let user = null;
            let technician = null;

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

            return { ...booking, user, technician, items };
        }));
    }
}
