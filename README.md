# TechFix Pro Manager

![Project Status](https://img.shields.io/badge/status-active-success.svg)
![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

> Hệ thống quản lý dịch vụ bảo trì và sửa chữa thiết bị công nghệ toàn diện.

TechFix Pro là giải pháp phần mềm hiện đại giúp các trung tâm bảo hành, sửa chữa quản lý quy trình nghiệp vụ từ khâu tiếp nhận, điều phối kỹ thuật viên, quản lý kho linh kiện đến chăm sóc khách hàng. Dự án được thiết kế để tối ưu hóa hiệu suất làm việc và nâng cao trải nghiệm khách hàng.

---

## 🚀 Tính năng chính

*   **Quản lý Đặt lịch (Booking)**: Khách hàng đặt lịch sửa chữa trực tuyến, theo dõi trạng thái đơn hàng thời gian thực.
*   **Điều phối Kỹ thuật viên**: Phân công công việc tự động hoặc thủ công, đánh giá hiệu suất kỹ thuật viên.
*   **Kho & Linh kiện (Inventory)**: Quản lý nhập/xuất kho, cảnh báo tồn kho thấp, quản lý danh mục linh kiện phong phú.
*   **Quản trị Tài khoản**: Phân quyền chi tiết cho Admin, Manager, Kỹ thuật viên và Khách hàng.
*   **Thống kê & Báo cáo**: Dashboard trực quan về doanh thu, số lượng đơn hàng, hiệu suất làm việc.

---

## 🛠 Công nghệ sử dụng

### Backend
*   **NestJS**: Framework Node.js mạnh mẽ, kiến trúc Microservices.
*   **TypeORM**: ORM linh hoạt hỗ trợ tương tác cơ sở dữ liệu.
*   **MySQL**: Cơ sở dữ liệu quan hệ mạnh mẽ, ổn định.
*   **Docker**: Đóng gói và triển khai ứng dụng nhất quán.

### Frontend
*   **Next.js 15**: Framework React hiện đại, tối ưu SEO và hiệu năng.
*   **TypeScript**: Đảm bảo tính an toàn và dễ bảo trì cho mã nguồn.
*   **Tailwind CSS**: Styling nhanh chóng, tùy biến cao.
*   **Shadcn UI**: Bộ component UI đẹp mắt, chuyên nghiệp.

---

## ⚙️ Hướng dẫn cài đặt

Để chạy được dự án, bạn cần cài đặt sẵn các công cụ sau:
*   [Node.js](https://nodejs.org/) (>= 20.x)
*   [Docker](https://www.docker.com/) & Docker Compose
*   [Git](https://git-scm.com/)

### 1. Cài đặt mã nguồn
Clone dự án về máy của bạn:

```bash
git clone https://github.com/NTQD/Fixtech.Pro.git
cd Fixtech.Pro
```

### 2. Cài đặt Backend
Di chuyển vào thư mục backend và cài đặt dependencies:

```bash
cd backend
npm install
```

**Cấu hình biến môi trường (`.env`):**
Copy file `env.example` thành `.env` và cập nhật các thông số kết nối Database (nếu chạy local không qua Docker) hoặc để mặc định nếu dùng Docker.

```bash
cp env.example .env
```

### 3. Cài đặt Frontend
Mở một terminal mới, di chuyển vào thư mục frontend:

```bash
cd ../frontend
npm install
```

---

## ▶️ Cách sử dụng

### Chạy bằng Docker (Khuyên dùng cho Backend)
Khởi động toàn bộ hệ thống Backend (Database + Services) chỉ với một lệnh:

```bash
cd backend
docker-compose up --build
```
*   Server API sẽ chạy tại: `http://localhost:3000`

### Chạy Frontend
```bash
cd frontend
npm run dev
```
*   Truy cập Web App tại: `http://localhost:3001` (hoặc cổng hiển thị trên terminal).

---

## 📂 Cấu trúc thư mục

```
techfix-pro/
├── backend/                # Mã nguồn Backend (NestJS Monorepo)
│   ├── apps/               # Các microservices (api-gateway, auth, booking...)
│   ├── scripts/            # Các script tiện ích (db seed, diag...)
│   └── docker/             # Cấu hình Docker
│
├── frontend/               # Mã nguồn Frontend (Next.js)
│   ├── app/                # App Router (Pages & Layouts)
│   ├── components/         # Reusable UI Components
│   └── lib/                # Utility functions
│
└── README.md               # Tài liệu hướng dẫn chính
```

---

## 🤝 Đóng góp

Chúng tôi rất hoan nghênh mọi đóng góp để dự án phát triển tốt hơn!
1.  Fork dự án.
2.  Tạo nhánh tính năng mới (`git checkout -b feature/AmazingFeature`).
3.  Commit thay đổi của bạn (`git commit -m 'Add some AmazingFeature'`).
4.  Push lên branch (`git push origin feature/AmazingFeature`).
5.  Mở một Pull Request.

---

## 📝 Giấy phép

Dự án này được cấp phép theo giấy phép [MIT](LICENSE).

---
*© 2025 TechFix Pro Team.*