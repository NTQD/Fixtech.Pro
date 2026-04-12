import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import * as cluster from 'cluster';
import * as os from 'os';

async function bootstrap() {
    const app = await NestFactory.create(AppModule);
    // Catalog Service runs on 3003
    await app.listen(3003);
    // console.log(`Catalog Service is running on: ${await app.getUrl()}`);
}

const clusterModule = cluster as any;
if (clusterModule.isPrimary || clusterModule.isMaster) {
    const numCPUs = Math.min(os.cpus().length, 8); // Max 8 workers to prevent DB connection overload
    console.log(`Catalog Service Primary server is running on PID: ${process.pid}`);
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
        console.log(`Catalog Service Worker started on PID: ${process.pid}`);
    });
}
