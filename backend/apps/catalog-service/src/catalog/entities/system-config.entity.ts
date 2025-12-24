import { Entity, Column, PrimaryColumn } from 'typeorm';

@Entity()
export class SystemConfig {
    @PrimaryColumn()
    key: string;

    @Column({ type: 'text', nullable: true })
    value: string;

    @Column({ nullable: true })
    description: string;
}
