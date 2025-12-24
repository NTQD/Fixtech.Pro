import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { ServeStaticModule } from '@nestjs/serve-static';
import { join } from 'path';

@Module({
    imports: [
        ServeStaticModule.forRoot(
            {
                rootPath: join(process.cwd(), 'public'),
                serveRoot: '/public',
                serveStaticOptions: {
                    index: false,
                },
            },
        ),
    ],
    controllers: [AppController],
    providers: [],
})
export class AppModule { }
