import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { CatalogController } from './catalog.controller';
import { CatalogService } from './catalog.service';
import { Part } from './entities/part.entity';
import { Service } from './entities/service.entity';

@Module({
    imports: [TypeOrmModule.forFeature([Service, Part])],
    controllers: [CatalogController],
    providers: [CatalogService],
    exports: [CatalogService],
})
export class CatalogModule { }
