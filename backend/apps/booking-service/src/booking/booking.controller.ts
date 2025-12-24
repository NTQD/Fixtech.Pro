import { Controller, Get, Post, Body, UseGuards, Request, Patch, Param, Query } from '@nestjs/common';
import { BookingService } from './booking.service';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { AuthGuard } from '@nestjs/passport';

@ApiTags('Booking')
@Controller('bookings')
export class BookingController {
    constructor(private readonly bookingService: BookingService) { }

    @Post()
    @UseGuards(AuthGuard('jwt'))
    @ApiBearerAuth()
    @ApiOperation({ summary: 'Create a new booking with transaction' })
    create(@Body() createBookingDto: any, @Request() req) {
        return this.bookingService.createBooking(createBookingDto, req.user.userId);
    }

    @Get()
    @UseGuards(AuthGuard('jwt'))
    @ApiBearerAuth()
    @ApiOperation({ summary: 'Get all bookings' })
    findAll(@Request() req) {
        return this.bookingService.findAll(req.user);
    }

    @Get('my-bookings')
    @UseGuards(AuthGuard('jwt'))
    @ApiBearerAuth()
    @ApiOperation({ summary: 'Get my bookings' })
    findMyBookings(@Request() req) {
        return this.bookingService.findMyBookings(req.user);
    }

    @Get('search')
    @UseGuards(AuthGuard('jwt')) // Add Guard
    @ApiBearerAuth()
    @ApiOperation({ summary: 'Search bookings by ID or Phone (Restricted)' })
    search(@Query('q') query: string, @Request() req) { // Add Request
        return this.bookingService.search(query, req.user); // Pass user
    }

    @Patch(':id/status')
    @UseGuards(AuthGuard('jwt'))
    @ApiBearerAuth()
    @ApiOperation({ summary: 'Update booking status' })
    updateStatus(@Param('id') id: number, @Body('status') status: string) {
        return this.bookingService.updateStatus(id, status);
    }

    @Patch(':id/assign')
    @UseGuards(AuthGuard('jwt'))
    @ApiBearerAuth()
    @ApiOperation({ summary: 'Assign technician to booking' })
    assignTechnician(@Param('id') id: number, @Body('technicianId') technicianId: number) {
        return this.bookingService.assignTechnician(id, technicianId);
    }
    @Post(':id/parts')
    @UseGuards(AuthGuard('jwt'))
    @ApiBearerAuth()
    @ApiOperation({ summary: 'Add part to booking' })
    addPart(@Param('id') id: number, @Body() body: { partId: number; quantity: number }) {
        return this.bookingService.addPartToBooking(id, body.partId, body.quantity);
    }

    @Post(':id/services')
    @UseGuards(AuthGuard('jwt'))
    @ApiBearerAuth()
    @ApiOperation({ summary: 'Add service to booking' })
    addService(@Param('id') id: number, @Body() body: { serviceId: string }) {
        return this.bookingService.addServiceToBooking(id, body.serviceId);
    }
    @Patch(':id/cancel')
    @ApiOperation({ summary: 'Cancel a booking' })
    cancel(@Param('id') id: number, @Request() req) {
        return this.bookingService.cancelBooking(id, req.user);
    }

    @Post(':id/rate')
    @UseGuards(AuthGuard('jwt'))
    @ApiBearerAuth()
    @ApiOperation({ summary: 'Rate a completed booking' })
    rate(@Param('id') id: number, @Body() body: { technician_rating: number, comment: string }, @Request() req) {
        return this.bookingService.rateBooking(id, body, req.user);
    }
}
