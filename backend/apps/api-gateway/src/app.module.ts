import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { ServeStaticModule } from '@nestjs/serve-static';
import { join } from 'path';

@Module({
    imports: [
        ServeStaticModule.forRoot({
            rootPath: join(__dirname, '..', '..', 'uploads'), // Go up from dist/apps/api-gateway/main.js to root
            serveRoot: '/uploads',
        }),
    ],
    controllers: [AppController],
    providers: [],
})
export class AppModule { }
