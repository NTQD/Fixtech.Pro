# Fixtech.Pro 🛠️

[English](#english) | [Tiếng Việt](#tiếng-việt)

---

<a name="english"></a>
## English

**Fixtech.Pro** is a modern Android application built to streamline the process of booking and managing appliance repair services. It provides a comprehensive platform connecting customers with professional technicians, overseen by an administrative dashboard.

### ✨ Features
* **Customer App:** Browse repair services (AC, Fridge, Washing Machine, etc.), book appointments, track repair status, and leave reviews.
* **Technician App:** View assigned jobs, update job statuses (Pending, In Progress, Completed), and manage work schedules.
* **Admin Dashboard:** Manage users (Customers, Technicians), oversee all bookings, manage service categories, and monitor platform activity.
* **Modern UI/UX:** Built entirely with Jetpack Compose following Material Design 3 guidelines.

### 🛠️ Tech Stack
* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose
* **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture principles
* **Dependency Injection:** Dagger Hilt
* **Networking:** Retrofit2 & OkHttp3
* **Local Database:** Room Database
* **Asynchronous Programming:** Kotlin Coroutines & Flow

### 🚀 Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/NTQD/Fixtech.Pro.git
   ```
2. **Open the project in Android Studio:**
   * Launch Android Studio.
   * Select `File > Open...` and choose the `Fixtech.Pro` directory.
3. **Configure Backend URL:**
   * Navigate to `app/src/main/java/vn/vibe/booking/data/remote/BackendConfig.kt`.
   * Update the `BASE_URL` to point to your running backend server (e.g., `http://<YOUR_IP>:9100`).
4. **Build and Run:**
   * Sync the project with Gradle files.
   * Connect an Android device or start an emulator.
   * Click the **Run** button (`Shift + F10`).

---

<a name="tiếng-việt"></a>
## Tiếng Việt

**Fixtech.Pro** là một ứng dụng Android hiện đại được xây dựng nhằm tối ưu hóa quy trình đặt lịch và quản lý các dịch vụ sửa chữa điện lạnh, điện gia dụng. Ứng dụng cung cấp một nền tảng toàn diện kết nối khách hàng với các kỹ thuật viên chuyên nghiệp, dưới sự quản lý của hệ thống quản trị viên (Admin).

### ✨ Tính năng nổi bật
* **Dành cho Khách hàng:** Xem các dịch vụ sửa chữa (Máy lạnh, Tủ lạnh, Máy giặt,...), đặt lịch hẹn, theo dõi trạng thái sửa chữa và đánh giá dịch vụ.
* **Dành cho Kỹ thuật viên:** Xem danh sách công việc được giao, cập nhật trạng thái công việc (Chờ xử lý, Đang thực hiện, Đã hoàn thành) và quản lý lịch trình.
* **Dành cho Admin:** Quản lý người dùng (Khách hàng, Kỹ thuật viên), quản lý tất cả các đơn đặt lịch, quản lý danh mục dịch vụ và theo dõi hoạt động của nền tảng.
* **Giao diện hiện đại (UI/UX):** Được xây dựng hoàn toàn bằng Jetpack Compose tuân theo tiêu chuẩn Material Design 3.

### 🛠️ Công nghệ sử dụng
* **Ngôn ngữ:** Kotlin
* **Giao diện (UI Toolkit):** Jetpack Compose
* **Kiến trúc:** MVVM (Model-View-ViewModel) kết hợp Clean Architecture
* **Tiêm phụ thuộc (DI):** Dagger Hilt
* **Giao tiếp mạng:** Retrofit2 & OkHttp3
* **Cơ sở dữ liệu cục bộ:** Room Database
* **Xử lý bất đồng bộ:** Kotlin Coroutines & Flow

### 🚀 Cài đặt & Khởi chạy

1. **Clone kho lưu trữ (repository):**
   ```bash
   git clone https://github.com/NTQD/Fixtech.Pro.git
   ```
2. **Mở dự án bằng Android Studio:**
   * Khởi chạy Android Studio.
   * Chọn `File > Open...` và trỏ tới thư mục `Fixtech.Pro`.
3. **Cấu hình URL Backend:**
   * Mở file `app/src/main/java/vn/vibe/booking/data/remote/BackendConfig.kt`.
   * Cập nhật `BASE_URL` thành địa chỉ IP server backend của bạn (ví dụ: `http://<IP_CỦA_BẠN>:9100`).
4. **Build và Chạy ứng dụng:**
   * Đồng bộ Gradle (Sync project with Gradle files).
   * Kết nối thiết bị Android thật hoặc mở máy ảo (Emulator).
   * Nhấn nút **Run** (`Shift + F10`).
