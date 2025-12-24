import { Injectable, NotFoundException } from '@nestjs/common';
import * as crypto from 'crypto';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Part } from './entities/part.entity';
import { Service } from './entities/service.entity';
import { SystemConfig } from './entities/system-config.entity';

@Injectable()
export class CatalogService {
    constructor(
        @InjectRepository(Service)
        private servicesRepository: Repository<Service>,
        @InjectRepository(Part)
        private partsRepository: Repository<Part>,
        @InjectRepository(SystemConfig)
        private configRepository: Repository<SystemConfig>,
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
        if (!data.id) {
            data.id = crypto.randomUUID();
        }
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

    async updatePart(id: number, data: Partial<Part>) {
        await this.partsRepository.update(id, data);
        return this.findOnePart(id);
    }

    async deletePart(id: number) {
        const result = await this.partsRepository.delete(id);
        if (result.affected === 0) {
            throw new NotFoundException(`Part ${id} not found`);
        }
        return { message: 'Part deleted successfully' };
    }

    // --- Config CRUD ---
    async getAllConfigs() {
        const configs = await this.configRepository.find();
        // Convert to Key-Value object
        return configs.reduce((acc, curr) => {
            acc[curr.key] = curr.value;
            return acc;
        }, {});
    }

    async updateConfigs(configs: Record<string, string>) {
        const promises = Object.entries(configs).map(async ([key, value]) => {
            let config = await this.configRepository.findOne({ where: { key } });
            if (config) {
                config.value = value;
            } else {
                config = this.configRepository.create({ key, value });
            }
            return this.configRepository.save(config);
        });
        await Promise.all(promises);
        return this.getAllConfigs();
    }
}
