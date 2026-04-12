import { Module } from '@nestjs/common';
import { CacheModule } from '@nestjs/cache-manager';
import { TypeOrmModule } from '@nestjs/typeorm';
import { CatalogController } from './catalog.controller';
import { CatalogService } from './catalog.service';
import { Part } from './entities/part.entity';
import { Service } from './entities/service.entity';
import { SystemConfig } from './entities/system-config.entity';

@Module({
    imports: [
        TypeOrmModule.forFeature([Service, Part, SystemConfig]),
        CacheModule.register({ ttl: 60000, max: 100 })
    ],
    controllers: [CatalogController],
    providers: [CatalogService],
    exports: [CatalogService],
})
export class CatalogModule { }
