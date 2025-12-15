import { Entity, Column, PrimaryGeneratedColumn, ManyToOne, JoinColumn } from 'typeorm';
import { Booking } from './booking.entity';

@Entity('booking_items')
export class BookingItem {
    @PrimaryGeneratedColumn('increment')
    id: number;

    @Column()
    booking_id: number;

    @ManyToOne(() => Booking, (booking) => booking.items, { onDelete: 'CASCADE' })
    @JoinColumn({ name: 'booking_id' })
    booking: Booking;

    // Decoupled: Service ID, no relation
    @Column({ nullable: true })
    service_id: string;

    // Decoupled: Part ID, no relation
    @Column({ nullable: true })
    part_id: number;

    @Column({ type: 'decimal', precision: 12, scale: 2 })
    price: number;

    @Column({ type: 'int', default: 1 })
    quantity: number;
}
