# TechFix Pro Backend

Hệ thống Backend quản lý đặt lịch sửa chữa, được xây dựng với **NestJS**, tuân thủ kiến trúc **Modular Monolith** và chuẩn bảo mật cao.

## Công nghệ sử dụng
- **Framework**: NestJS (TypeScript)
- **Database**: MySQL (đã chuyển từ SQL Server)
- **ORM**: TypeORM
- **Authentication**: JWT, BCrypt
- **Containerization**: Docker, Docker Compose

## Kiến trúc Module
Dự án được chia thành 3 modules chính:
1. **Account Module**: Quản lý User, Authentication (Login/Register).
2. **Catalog Module**: Quản lý Dịch vụ (Services) và Linh kiện (Parts).
3. **Booking Module**: Quản lý Đặt lịch, tích hợp Transaction ACID (trừ kho + tạo đơn).

## Hướng dẫn chạy (Run)

### Cách 1: Chạy bằng Docker (Khuyên dùng)
Chạy cả Database MySQL và Backend API:
```bash
docker-compose up --build
```
- API sẽ chạy tại: `http://localhost:3000`
- Swagger Docs: `http://localhost:3000/api/docs`

### Cách 2: Chạy thủ công (Development)
1. Cài đặt dependencies:
```bash
npm install
```
2. Cấu hình Database trong `.env` hoặc `app.module.ts`.
3. Chạy Server:
```bash
npm run start:dev
```

## Kiểm thử (Testing)
Chạy Unit Test cho các logic quan trọng (Token, Booking Transaction):
```bash
npm run test
```
