import { Entity, Column, PrimaryGeneratedColumn, CreateDateColumn, UpdateDateColumn, OneToMany } from 'typeorm';
import { BookingItem } from './booking-item.entity';
import { BookingHistory } from './booking-history.entity';
import { BookingStatus } from './booking-status.enum';

@Entity('bookings')
export class Booking {
    @PrimaryGeneratedColumn('increment')
    id: number;

    // Decoupled: User ID only, no foreign key constraint to Users table
    @Column()
    user_id: number;

    // Decoupled: Technician ID only
    @Column({ nullable: true })
    technician_id: number;

    @Column()
    customer_name: string;

    @Column()
    customer_phone: string;

    @Column({ nullable: true })
    customer_email: string;

    @Column({ type: 'text', nullable: true })
    device_info: string;

    @Column({ type: 'text', nullable: true })
    issue_description: string;

    @Column({ type: 'date' })
    scheduled_date: Date;

    @Column({ type: 'time' })
    scheduled_time: string;

    @Column({
        type: 'enum',
        enum: BookingStatus,
        default: BookingStatus.PENDING,
    })
    status: BookingStatus;

    @Column({ type: 'decimal', precision: 12, scale: 2, default: 0 })
    total_amount: number;

    @OneToMany(() => BookingItem, (item) => item.booking, { cascade: true })
    items: BookingItem[];

    @OneToMany(() => BookingHistory, (history) => history.booking, { cascade: true })
    history: BookingHistory[];

    @CreateDateColumn()
    created_at: Date;

    @UpdateDateColumn()
    updated_at: Date;
}
