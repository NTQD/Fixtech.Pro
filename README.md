# TechFix Pro - Microservices Architecture

## How to Run
### Backend Microservices (Monorepo)
1.  **Install dependencies**:
    ```bash
    cd backend
    npm install
    ```
2.  **Start Services**:
    - **API Gateway** (Port 3000): `npm run start api-gateway`
    - **Monolith** (Port 3001): `npm run start monolith`
    - **Auth Service** (Port 3002): `npm run start auth-service`
    - **Catalog Service** (Port 3003): `npm run start catalog-service`
    - **Booking Service** (Port 3004): `npm run start booking-service`
    
    *Note: For local development, you can run them in separate terminals.*

3.  **Docker Compose**:
    ```bash
    cd backend
    docker-compose -f docker-compose.microservices.yml up --build
    ```

### Frontend
1.  **Install**: `npm install`
2.  **Run**: `npm run dev` (Runs on port 3001 by default if 3000 is taken, or configure Next.js port).
    - Ensure Frontend connects to `http://localhost:3000` (Gateway).

## Architecture
- **Apps**: `apps/api-gateway`, `apps/monolith`, `apps/auth-service`, `apps/catalog-service`, `apps/booking-service`
- **Shared Lib**: `libs/common` (Entities & Auth)
- **Database**: Shared MySQL Instance (Logical separation Support)