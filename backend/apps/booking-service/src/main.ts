import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

async function bootstrap() {
    const app = await NestFactory.create(AppModule);
    // Booking Service runs on 3004
    await app.listen(3004);
    console.log(`Booking Service is running on: ${await app.getUrl()}`);
}
bootstrap();
