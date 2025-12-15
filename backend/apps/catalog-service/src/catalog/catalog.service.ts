import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Part } from './entities/part.entity';
import { Service } from './entities/service.entity'; // Updated import

@Injectable()
export class CatalogService {
    constructor(
        @InjectRepository(Service)
        private servicesRepository: Repository<Service>,
        @InjectRepository(Part)
        private partsRepository: Repository<Part>,
    ) { }

    // --- Services CRUD ---
    findAllServices() {
        return this.servicesRepository.find({ where: { is_active: true } });
    }

    async findOneService(id: string) {
        const service = await this.servicesRepository.findOne({ where: { id } });
        if (!service) throw new NotFoundException(`Service ${id} not found`);
        return service;
    }

    createService(data: Partial<Service>) {
        const service = this.servicesRepository.create(data);
        return this.servicesRepository.save(service);
    }

    // --- Parts CRUD ---
    findAllParts() {
        return this.partsRepository.find();
    }

    async findOnePart(id: number) {
        const part = await this.partsRepository.findOne({ where: { id } });
        if (!part) throw new NotFoundException(`Part ${id} not found`);
        return part;
    }

    createPart(data: Partial<Part>) {
        const part = this.partsRepository.create(data);
        return this.partsRepository.save(part);
    }

    async updateStock(id: number, quantityChange: number) {
        const part = await this.findOnePart(id);
        part.stock += quantityChange;
        return this.partsRepository.save(part);
    }
}
