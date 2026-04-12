import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { createProxyMiddleware } from 'http-proxy-middleware';
import * as cluster from 'cluster';
import * as os from 'os';

async function bootstrap() {
    const app = await NestFactory.create(AppModule);
    app.enableCors(); // Enable CORS for Frontend Access

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
    // console.log(`API Gateway is running on: ${await app.getUrl()}`);
}

const clusterModule = cluster as any;
if (clusterModule.isPrimary || clusterModule.isMaster) {
    const numCPUs = Math.min(os.cpus().length, 8); // Max 8 workers to prevent memory overload
    console.log(`API Gateway Primary server is running on PID: ${process.pid}`);
    console.log(`Spawning ${numCPUs} worker processes for multi-threaded handling...`);

    for (let i = 0; i < numCPUs; i++) {
        clusterModule.fork();
    }

    clusterModule.on('exit', (worker, code, signal) => {
        console.log(`Worker ${worker.process.pid} died. Restarting...`);
        clusterModule.fork();
    });
} else {
    bootstrap().then(() => {
        console.log(`API Gateway Worker started on PID: ${process.pid}`);
    });
}
