import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { createProxyMiddleware } from 'http-proxy-middleware';

async function bootstrap() {
    const app = await NestFactory.create(AppModule);

    // Proxy Configuration for Microservices
    const services = {
        '/auth': process.env.AUTH_SERVICE_URL || 'http://localhost:3002',
        '/users': process.env.AUTH_SERVICE_URL || 'http://localhost:3002',
        '/catalog': process.env.CATALOG_SERVICE_URL || 'http://localhost:3003',
        '/bookings': process.env.BOOKING_SERVICE_URL || 'http://localhost:3004',
    };

    Object.keys(services).forEach(path => {
        app.use(path, createProxyMiddleware({
            target: services[path],
            changeOrigin: true,
            pathRewrite: (pathStr) => pathStr // Keep path as is
        }));
    });

    // Gateway runs on 3000
    await app.listen(3000);
    console.log(`API Gateway is running on: ${await app.getUrl()}`);
}
bootstrap();
