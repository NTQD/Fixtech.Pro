import { Controller, Get, Post, Body, Param, UseGuards } from '@nestjs/common';
import { CatalogService } from './catalog.service';
import { Part } from './entities/part.entity';
import { Service } from './entities/service.entity';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { AuthGuard } from '@nestjs/passport';

@ApiTags('Catalog')
@Controller('catalog')
export class CatalogController {
    constructor(private readonly catalogService: CatalogService) { }

    @Get('services')
    @ApiOperation({ summary: 'List all active services' })
    getServices() {
        return this.catalogService.findAllServices();
    }

    @Post('services')
    @ApiBearerAuth()
    @UseGuards(AuthGuard('jwt'))
    @ApiOperation({ summary: 'Create a new service (Admin only)' })
    createService(@Body() createServiceDto: any) {
        return this.catalogService.createService(createServiceDto);
    }

    @Get('services/:id')
    @ApiOperation({ summary: 'Get service by ID' })
    getService(@Param('id') id: string) {
        return this.catalogService.findOneService(id);
    }

    @Get('parts')
    @ApiOperation({ summary: 'List all parts' })
    getParts() {
        return this.catalogService.findAllParts();
    }

    @Get('parts/:id')
    @ApiOperation({ summary: 'Get part by ID' })
    getPart(@Param('id') id: string) {
        return this.catalogService.findOnePart(Number(id));
    }

    @Post('parts')
    @ApiBearerAuth()
    @UseGuards(AuthGuard('jwt'))
    @ApiOperation({ summary: 'Create new part (Admin only)' })
    createPart(@Body() createPartDto: any) {
        return this.catalogService.createPart(createPartDto);
    }
}
