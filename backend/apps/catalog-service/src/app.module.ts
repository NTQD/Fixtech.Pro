import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { PassportModule } from '@nestjs/passport';
import { JwtModule } from '@nestjs/jwt';
import { CatalogModule } from './catalog/catalog.module';
import { Service } from './catalog/entities/service.entity';
import { Part } from './catalog/entities/part.entity';
import { JwtStrategy } from './auth/jwt.strategy';

@Module({
    imports: [
        ConfigModule.forRoot({
            isGlobal: true,
            envFilePath: '../../.env'
        }),
        TypeOrmModule.forRoot({
            type: 'mysql',
            host: process.env.DB_HOST || 'localhost',
            port: parseInt(process.env.DB_PORT, 10) || 3306,
            username: process.env.DB_USERNAME || 'root',
            password: process.env.DB_PASSWORD || 'rootpassword',
            database: process.env.DB_DATABASE_CATALOG || 'techfix_catalog',
            entities: [Service, Part],
            synchronize: true,
            autoLoadEntities: true,
        }),
        PassportModule,
        JwtModule.registerAsync({
            imports: [ConfigModule],
            inject: [ConfigService],
            useFactory: async (configService: ConfigService) => ({
                secret: configService.get<string>('JWT_SECRET') || 'super-secret-key',
                signOptions: { expiresIn: '1d' },
            }),
        }),
        CatalogModule,
    ],
    providers: [JwtStrategy],
})
export class AppModule { }
