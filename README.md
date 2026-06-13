# Fixtech.Pro 🛠️

[English](#english) | [Tiếng Việt](#tiếng-việt)

---

<a name="english"></a>
## English

**Fixtech.Pro** is an ecosystem built to booking and managing home appliance repair services. It connects customers with professional technicians via a mobile app and is managed by administrators using a backend control panel.

### 🏛️ Project Architecture
The project is divided into two main components:
1. **Backend Service (`be`)**: A Spring Boot application connected to a MySQL database, running in Docker containers.
2. **Android Client (`app`)**: A native Kotlin application built using Jetpack Compose (MVVM + Clean Architecture).

---

### 🚀 Setup & Installation Guide

#### 1. Backend Service Setup (Docker)

##### **Prerequisites**
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running.
* [Docker Compose](https://docs.docker.com/compose/) installed.

##### **Steps**
1. Navigate to the root directory of the project:
   ```bash
   cd Fixtech.Pro
   ```
2. Configure the environment variables by editing the `.env` file in the root directory:
   * Specify ports (e.g., `MYSQL_PORT=3307`, `PHPMYADMIN_PORT=8081`).
   * Specify database credentials (e.g., `MYSQL_ROOT_PASSWORD=root`, `MYSQL_DATABASE=ecom`).
3. Start the services using Docker Compose:
   ```bash
   docker-compose up -d --build
   ```
4. Verify the running containers:
   ```bash
   docker ps
   ```
   You should see 3 running containers:
   * `app-mobile` (Spring Boot API server on port `9100`)
   * `app-mobile-mysql` (MySQL database on port `3307`)
   * `app-mobile-phpmyadmin` (Database browser on port `8081`)
5. The database is automatically initialized using `be/init-db.sql`. You can access and manage it by visiting `http://localhost:8081` in your browser.

---

#### 2. Android Client Setup (Android Studio)

##### **Prerequisites**
* Android Studio (Jellyfish / Koala or newer).
* JDK 17 configured in Android Studio.
* Android SDK (API 33+).

##### **Steps**
1. Launch Android Studio.
2. Select **File > Open...** and select the `Fixtech.Pro` root directory.
3. Sync the project with Gradle files (**File > Sync Project with Gradle Files**).
4. Configure the server IP connection:
   * Open the file [BackendConfig.kt](file:///e:/Mobile_App/AndroidStudioProjects/Fixtech.Pro/app/src/main/java/vn/vibe/booking/data/remote/BackendConfig.kt):
     ```kotlin
     package vn.vibe.booking.data.remote

     object BackendConfig {
         const val BASE_URL = "http://<YOUR_LOCAL_IP>:9100"
     }
     ```
   * Replace `<YOUR_LOCAL_IP>` with your computer's local IP address (e.g., `192.168.1.50`). 
   * *Note: If you are running on the Android Emulator on the same computer as the backend, you can use `http://10.0.2.2:9100`.*
5. Build and run the app:
   * Connect an Android device (with USB Debugging enabled) or start an Android Emulator.
   * Click the **Run** button (`Shift + F10`) to deploy.

---

<a name="tiếng-việt"></a>
## Tiếng Việt

**Fixtech.Pro** là một hệ sinh thái hỗ trợ đặt lịch và quản lý các dịch vụ sửa chữa thiết bị gia dụng. Hệ thống kết nối khách hàng với kỹ thuật viên thông qua ứng dụng di động, kết hợp với bảng quản trị vận hành bởi máy chủ backend.

### 🏛️ Kiến trúc dự án
Dự án được chia thành hai phần chính:
1. **Dịch vụ Backend (`be`)**: Ứng dụng Spring Boot kết nối cơ sở dữ liệu MySQL, được đóng gói và chạy bằng Docker.
2. **Ứng dụng Android (`app`)**: Ứng dụng di động viết bằng Kotlin sử dụng Jetpack Compose (mô hình MVVM + Clean Architecture).

---

### 🚀 Hướng dẫn cài đặt & Khởi chạy

#### 1. Cài đặt và chạy Backend (Docker)

##### **Yêu cầu hệ thống**
* Đã cài đặt [Docker Desktop](https://www.docker.com/products/docker-desktop/) và đang chạy.
* Đã cài đặt [Docker Compose](https://docs.docker.com/compose/).

##### **Các bước thực hiện**
1. Di chuyển vào thư mục gốc của dự án:
   ```bash
   cd Fixtech.Pro
   ```
2. Cấu hình các biến môi trường trong file `.env` ở thư mục gốc:
   * Thiết lập cổng kết nối (ví dụ: `MYSQL_PORT=3307`, `PHPMYADMIN_PORT=8081`).
   * Thiết lập thông tin cơ sở dữ liệu (ví dụ: `MYSQL_ROOT_PASSWORD=root`, `MYSQL_DATABASE=ecom`).
3. Khởi chạy các dịch vụ thông qua Docker Compose:
   ```bash
   docker-compose up -d --build
   ```
4. Kiểm tra trạng thái các container đang hoạt động:
   ```bash
   docker ps
   ```
   Bạn sẽ thấy 3 container đang chạy:
   * `app-mobile` (Spring Boot API Server chạy ở cổng `9100`)
   * `app-mobile-mysql` (MySQL Database chạy ở cổng `3307`)
   * `app-mobile-phpmyadmin` (Trình quản trị DB chạy ở cổng `8081`)
5. Cơ sở dữ liệu sẽ tự động được khởi tạo dữ liệu mẫu thông qua file `be/init-db.sql`. Bạn có thể truy cập trình quản lý tại địa chỉ `http://localhost:8081`.

---

#### 2. Cài đặt và khởi chạy Ứng dụng Android (Android Studio)

##### **Yêu cầu hệ thống**
* Android Studio (Phiên bản Jellyfish / Koala hoặc mới hơn).
* Đã cấu hình JDK 17 trong Android Studio.
* Android SDK (API 33 trở lên).

##### **Các bước thực hiện**
1. Mở Android Studio.
2. Chọn **File > Open...** và dẫn tới thư mục gốc `Fixtech.Pro`.
3. Chờ dự án đồng bộ Gradle hoàn tất (**File > Sync Project with Gradle Files**).
4. Cấu hình kết nối tới server backend:
   * Mở file [BackendConfig.kt](file:///e:/Mobile_App/AndroidStudioProjects/Fixtech.Pro/app/src/main/java/vn/vibe/booking/data/remote/BackendConfig.kt):
     ```kotlin
     package vn.vibe.booking.data.remote

     object BackendConfig {
         const val BASE_URL = "http://<IP_CỦA_BẠN>:9100"
     }
     ```
   * Thay thế `<IP_CỦA_BẠN>` bằng địa chỉ IP mạng nội bộ của máy tính bạn (ví dụ: `192.168.1.50`).
   * *Lưu ý: Nếu chạy bằng máy ảo Android Emulator trên cùng máy tính chứa backend, bạn có thể sử dụng `http://10.0.2.2:9100`.*
5. Build và chạy ứng dụng:
   * Kết nối thiết bị Android thật (đã bật Gỡ lỗi USB) hoặc khởi động máy ảo Android (Emulator).
   * Nhấn nút **Run** (`Shift + F10`) để cài đặt ứng dụng.
