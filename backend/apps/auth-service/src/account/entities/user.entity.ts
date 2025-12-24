import { Entity, Column, PrimaryGeneratedColumn, CreateDateColumn, UpdateDateColumn } from 'typeorm';
import { Exclude } from 'class-transformer';

export enum UserRole {
    CUSTOMER = 'CUSTOMER',
    ADMIN = 'ADMIN',
    TECHNICIAN = 'TECHNICIAN',
}

@Entity('users')
export class User {
    @PrimaryGeneratedColumn('increment')
    id: number;

    @Column({ unique: true })
    email: string;

    @Column({ unique: true, nullable: true })
    phone: string;

    @Column({ nullable: true })
    @Exclude() // Don't return password in responses
    password_hash: string;

    @Column()
    full_name: string;

    @Column({
        type: 'enum',
        enum: UserRole,
        default: UserRole.CUSTOMER,
    })
    role: UserRole;

    @Column({ nullable: true })
    avatar_url: string;

    @Column({ default: 1 })
    status: number;

    @Column({ type: 'decimal', precision: 3, scale: 1, default: 0 })
    reputation_score: number;

    @Column({ default: 0 })
    total_stars: number;

    @Column({ default: 0 })
    total_rated_orders: number;

    @CreateDateColumn()
    created_at: Date;

    @UpdateDateColumn()
    updated_at: Date;
}
