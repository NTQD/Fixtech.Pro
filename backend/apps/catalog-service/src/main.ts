import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

async function bootstrap() {
    const app = await NestFactory.create(AppModule);
    // Catalog Service runs on 3003
    await app.listen(3003);
    console.log(`Catalog Service is running on: ${await app.getUrl()}`);
}
bootstrap();
