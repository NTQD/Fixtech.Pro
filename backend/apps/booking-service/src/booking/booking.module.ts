import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { HttpModule } from '@nestjs/axios';
import { Booking } from './entities/booking.entity';
import { BookingItem } from './entities/booking-item.entity';
import { BookingService } from './booking.service';
import { BookingController } from './booking.controller';

import { BookingHistory } from './entities/booking-history.entity';

@Module({
    imports: [
        TypeOrmModule.forFeature([Booking, BookingItem, BookingHistory]),
        HttpModule,
    ],
    controllers: [BookingController],
    providers: [BookingService],
    exports: [BookingService],
})
export class BookingModule { }
