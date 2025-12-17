import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { AccountModule } from './account/account.module';
import { User } from './account/entities/user.entity';

@Module({
    imports: [
        ConfigModule.forRoot({
            isGlobal: true,
            envFilePath: '../../.env' // Path to root .env
        }),
        TypeOrmModule.forRoot({
            type: 'mysql',
            host: process.env.DB_HOST || 'localhost',
            port: parseInt(process.env.DB_PORT, 10) || 3306,
            username: process.env.DB_USERNAME || 'root',
            password: process.env.DB_PASSWORD || 'rootpassword',
            database: process.env.DB_DATABASE_AUTH || 'techfix_auth',
            entities: [User], // Register Shared User Entity
            synchronize: false,
            autoLoadEntities: true,
        }),
        AccountModule,
    ],
    controllers: [],
    providers: [],
})
export class AppModule { }
