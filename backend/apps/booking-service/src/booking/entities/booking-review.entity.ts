import { Entity, Column, PrimaryGeneratedColumn, CreateDateColumn, OneToOne, JoinColumn } from 'typeorm';
import { Booking } from './booking.entity';

@Entity('booking_reviews')
export class BookingReview {
    @PrimaryGeneratedColumn('increment')
    id: number;

    @Column()
    booking_id: number;

    @OneToOne(() => Booking)
    @JoinColumn({ name: 'booking_id' })
    booking: Booking;

    @Column({ type: 'int' })
    technician_rating: number; // 1-5

    @Column({ type: 'text', nullable: true })
    comment: string;

    @CreateDateColumn()
    created_at: Date;
}
