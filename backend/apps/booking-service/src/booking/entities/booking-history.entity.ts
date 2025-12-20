import { Entity, Column, PrimaryGeneratedColumn, CreateDateColumn, ManyToOne, JoinColumn } from 'typeorm';
import { Booking } from './booking.entity';
import { BookingStatus } from './booking-status.enum';

@Entity('booking_history')
export class BookingHistory {
    @PrimaryGeneratedColumn()
    id: number;

    @Column()
    booking_id: number;

    @ManyToOne(() => Booking, (booking) => booking.history)
    @JoinColumn({ name: 'booking_id' })
    booking: Booking;

    @Column({
        type: 'enum',
        enum: BookingStatus,
    })
    status: BookingStatus;

    @Column({ nullable: true })
    note: string;

    @CreateDateColumn()
    created_at: Date;
}
