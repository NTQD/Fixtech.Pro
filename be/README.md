# Laptop Repair Booking System API Design

Tài liệu này mô tả bộ API cho hệ thống đặt lịch sửa laptop theo mô hình backend hiện tại của project (`Spring Boot` + `jOOQ` + `MySQL`).

> Ghi chú: đây là bản thiết kế chi tiết API và database. Phần triển khai logic nghiệp vụ có thể được bổ sung sau.

---

## 1. Mục tiêu hệ thống

Hệ thống hỗ trợ các chức năng chính:

- Đăng ký, đăng nhập, xem thông tin tài khoản.
- Quản lý dịch vụ sửa laptop.
- Quản lý đặt lịch sửa laptop.
- Cập nhật trạng thái booking theo quy trình làm việc.
- Quản lý ghi chú, lịch sử trạng thái và đánh giá sau dịch vụ.
- Quản trị viên có thể quản lý danh mục, kỹ thuật viên, lịch làm việc và booking.

---

## 2. Vai trò người dùng

### 2.1 `USER`
Khách hàng đặt lịch sửa laptop.

Chức năng:
- Đăng ký / đăng nhập
- Tạo booking
- Xem danh sách booking của mình
- Hủy booking
- Đánh giá dịch vụ

### 2.2 `TECHNICIAN`
Kỹ thuật viên tiếp nhận và xử lý booking.

Chức năng:
- Xem booking được phân công
- Cập nhật trạng thái xử lý
- Ghi chú tình trạng máy, báo giá, kết quả sửa chữa

### 2.3 `ADMIN`
Quản trị viên hệ thống.

Chức năng:
- Quản lý người dùng
- Quản lý danh mục dịch vụ
- Quản lý dịch vụ sửa chữa
- Quản lý booking và phân công kỹ thuật viên
- Cập nhật trạng thái tổng thể

---

## 3. Quy ước response chung

### 3.1 Success response
```json
{
  "success": true,
  "message": "OK",
  "data": {}
}
```

### 3.2 Pagination response
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "items": [],
    "page": 1,
    "limit": 20,
    "total": 100
  }
}
```

### 3.3 Error response
```json
{
  "success": false,
  "message": "INVALID_AUTHENTICATION_INFO",
  "errors": []
}
```

---

## 4. Database schema

Các bảng chính:

- `user`
- `repair_service_category`
- `repair_service`
- `repair_booking`
- `repair_booking_item`
- `repair_booking_status_history`
- `repair_booking_note`
- `repair_review`

Chi tiết script MySQL nằm trong `init-db.sql`.

---

## 5. API chi tiết

# 5.1 Authentication API

## 5.1.1 Đăng ký
**POST** `/authenticate/register`

### Input
```json
{
  "name": "Nguyen Van A",
  "phone": "0901234567",
  "email": "a@gmail.com",
  "password": "123456",
  "plainPassword": "123456",
  "role": "USER"
}
```

### Output
```json
{
  "success": true,
  "message": "Đăng ký thành công",
  "data": {
    "success": true,
    "message": "Đăng ký thành công"
  }
}
```

### Chức năng
- Tạo mới tài khoản khách hàng.
- Kiểm tra trùng `phone` / `email`.
- Mặc định `active = true`.

---

## 5.1.2 Đăng nhập
**POST** `/authenticate/login`

### Input
```json
{
  "phone": "0901234567",
  "email": null,
  "password": "123456"
}
```

### Output
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "accessToken": "jwt-token"
  }
}
```

### Chức năng
- Đăng nhập bằng `phone` hoặc `email`.
- Kiểm tra mật khẩu.
- Trả về JWT token.

---

## 5.1.3 Lấy thông tin tôi
**GET** `/authenticate/me`

### Header
```http
Authorization: Bearer <token>
```

### Output
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "id": 1,
    "name": "Nguyen Van A",
    "phone": "0901234567",
    "email": "a@gmail.com",
    "role": "USER",
    "active": true
  }
}
```

### Chức năng
- Trả về thông tin tài khoản đang đăng nhập.

---

## 5.1.4 Đổi mật khẩu
**POST** `/users/me/change-password`

### Input
```json
{
  "oldPassword": "123456",
  "newPassword": "123456789"
}
```

### Output
```json
{
  "success": true,
  "message": "Đổi mật khẩu thành công",
  "data": null
}
```

### Chức năng
- Xác thực mật khẩu cũ.
- Không cho phép mật khẩu mới trùng mật khẩu cũ.

---

# 5.2 Service Category API

## 5.2.1 Danh sách danh mục dịch vụ
**GET** `/admin/service-categories`

### Query
- `keyword` optional
- `page` default 1
- `limit` default 20

### Output
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "items": [
      {
        "id": 1,
        "name": "Màn hình",
        "description": "Sửa thay màn hình laptop",
        "active": true
      }
    ],
    "page": 1,
    "limit": 20,
    "total": 1
  }
}
```

### Chức năng
- Tìm kiếm theo tên.
- Phục vụ quản trị danh mục dịch vụ.

---

## 5.2.2 Tạo danh mục
**POST** `/admin/service-categories`

### Input
```json
{
  "name": "Bàn phím",
  "description": "Sửa thay bàn phím laptop",
  "active": true
}
```

### Output
```json
{
  "success": true,
  "message": "Tạo danh mục thành công",
  "data": {
    "id": 2,
    "name": "Bàn phím",
    "description": "Sửa thay bàn phím laptop",
    "active": true
  }
}
```

### Chức năng
- Tạo mới danh mục dịch vụ.

---

## 5.2.3 Cập nhật danh mục
**PUT** `/admin/service-categories/{id}`

### Input
```json
{
  "name": "Pin",
  "description": "Sửa thay pin laptop",
  "active": true
}
```

### Output
```json
{
  "success": true,
  "message": "Cập nhật thành công",
  "data": null
}
```

---

## 5.2.4 Xóa danh mục
**DELETE** `/admin/service-categories/{id}`

### Output
```json
{
  "success": true,
  "message": "Xóa thành công",
  "data": null
}
```

---

# 5.3 Repair Service API

## 5.3.1 Danh sách dịch vụ
**GET** `/services`

### Query
- `keyword`
- `categoryId`
- `page`
- `limit`

### Output
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "items": [
      {
        "id": 1,
        "categoryId": 1,
        "name": "Thay màn hình Dell",
        "shortDescription": "Thay màn hình chính hãng",
        "basePrice": 2500000,
        "estimatedMinutes": 90,
        "active": true
      }
    ],
    "page": 1,
    "limit": 20,
    "total": 1
  }
}
```

---

## 5.3.2 Chi tiết dịch vụ
**GET** `/services/{id}`

### Output
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "id": 1,
    "categoryId": 1,
    "name": "Thay màn hình Dell",
    "description": "Chi tiết mô tả...",
    "basePrice": 2500000,
    "estimatedMinutes": 90,
    "warrantyDays": 90,
    "active": true
  }
}
```

---

## 5.3.3 Tạo dịch vụ
**POST** `/admin/services`

### Input
```json
{
  "categoryId": 1,
  "name": "Thay SSD laptop",
  "shortDescription": "Nâng cấp SSD",
  "description": "Nội dung chi tiết",
  "basePrice": 1200000,
  "estimatedMinutes": 60,
  "warrantyDays": 180,
  "active": true
}
```

### Output
```json
{
  "success": true,
  "message": "Tạo dịch vụ thành công",
  "data": {
    "id": 10
  }
}
```

---

## 5.3.4 Cập nhật dịch vụ
**PUT** `/admin/services/{id}`

### Input
```json
{
  "categoryId": 1,
  "name": "Thay SSD laptop",
  "shortDescription": "Nâng cấp SSD",
  "description": "Nội dung chi tiết",
  "basePrice": 1300000,
  "estimatedMinutes": 60,
  "warrantyDays": 180,
  "active": true
}
```

### Output
```json
{
  "success": true,
  "message": "Cập nhật thành công",
  "data": null
}
```

---

## 5.3.5 Xóa dịch vụ
**DELETE** `/admin/services/{id}`

### Output
```json
{
  "success": true,
  "message": "Xóa thành công",
  "data": null
}
```

---

# 5.4 Booking API

## 5.4.1 Tạo booking
**POST** `/bookings`

### Input
```json
{
  "customerName": "Nguyen Van A",
  "customerPhone": "0901234567",
  "customerEmail": "a@gmail.com",
  "deviceType": "Laptop Dell Inspiron",
  "deviceBrand": "Dell",
  "deviceModel": "Inspiron 15",
  "issueDescription": "Máy không lên nguồn",
  "preferredDate": "2026-05-20",
  "preferredTimeSlot": "09:00-11:00",
  "address": "123 Nguyen Trai, HCM",
  "note": "Gọi trước khi tới",
  "items": [
    {
      "serviceId": 1,
      "quantity": 1
    }
  ]
}
```

### Output
```json
{
  "success": true,
  "message": "Tạo lịch thành công",
  "data": {
    "id": 1001,
    "bookingCode": "BK202605200001",
    "status": "PENDING_CONFIRMATION",
    "totalEstimatedPrice": 2500000,
    "totalEstimatedMinutes": 90
  }
}
```

### Chức năng
- Tạo lịch sửa laptop.
- Ghi nhận thông tin máy, lỗi, khung giờ mong muốn.
- Tạo các item dịch vụ trong booking.

---

## 5.4.2 Danh sách booking của tôi
**GET** `/bookings/me`

### Query
- `status` optional
- `page`
- `limit`

### Output
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "items": [
      {
        "id": 1001,
        "bookingCode": "BK202605200001",
        "status": "PENDING_CONFIRMATION",
        "preferredDate": "2026-05-20",
        "preferredTimeSlot": "09:00-11:00",
        "totalEstimatedPrice": 2500000
      }
    ],
    "page": 1,
    "limit": 20,
    "total": 1
  }
}
```

---

## 5.4.3 Chi tiết booking
**GET** `/bookings/{id}`

### Output
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "id": 1001,
    "bookingCode": "BK202605200001",
    "customerName": "Nguyen Van A",
    "customerPhone": "0901234567",
    "deviceType": "Laptop Dell Inspiron",
    "issueDescription": "Máy không lên nguồn",
    "status": "PENDING_CONFIRMATION",
    "items": [
      {
        "serviceId": 1,
        "serviceName": "Thay màn hình Dell",
        "quantity": 1,
        "estimatedPrice": 2500000
      }
    ],
    "statusHistory": [
      {
        "fromStatus": null,
        "toStatus": "PENDING_CONFIRMATION",
        "note": "Tạo booking",
        "createdAt": "2026-05-20T09:10:00"
      }
    ]
  }
}
```

---

## 5.4.4 Hủy booking
**POST** `/bookings/{id}/cancel`

### Input
```json
{
  "reason": "Không còn nhu cầu"
}
```

### Output
```json
{
  "success": true,
  "message": "Hủy booking thành công",
  "data": null
}
```

---

## 5.4.5 Xác nhận booking
**POST** `/admin/bookings/{id}/confirm`

### Input
```json
{
  "note": "Đã gọi xác nhận với khách"
}
```

### Output
```json
{
  "success": true,
  "message": "Xác nhận thành công",
  "data": null
}
```

---

## 5.4.6 Phân công kỹ thuật viên
**POST** `/admin/bookings/{id}/assign-technician`

### Input
```json
{
  "technicianId": 5,
  "note": "Phân cho kỹ thuật viên ca sáng"
}
```

### Output
```json
{
  "success": true,
  "message": "Phân công thành công",
  "data": null
}
```

---

## 5.4.7 Cập nhật trạng thái booking
**POST** `/technician/bookings/{id}/status`

### Input
```json
{
  "status": "IN_PROGRESS",
  "note": "Đang kiểm tra nguồn"
}
```

### Output
```json
{
  "success": true,
  "message": "Cập nhật trạng thái thành công",
  "data": null
}
```

### Danh sách status đề xuất
- `PENDING_CONFIRMATION`
- `CONFIRMED`
- `ASSIGNED`
- `IN_PROGRESS`
- `WAITING_PARTS`
- `COMPLETED`
- `CANCELLED`
- `REJECTED`

---

## 5.4.8 Thêm ghi chú booking
**POST** `/technician/bookings/{id}/notes`

### Input
```json
{
  "note": "Máy cần thay IC nguồn"
}
```

### Output
```json
{
  "success": true,
  "message": "Thêm ghi chú thành công",
  "data": null
}
```

---

# 5.5 Review API

## 5.5.1 Tạo đánh giá
**POST** `/bookings/{id}/review`

### Input
```json
{
  "rating": 5,
  "comment": "Dịch vụ tốt, xử lý nhanh"
}
```

### Output
```json
{
  "success": true,
  "message": "Đánh giá thành công",
  "data": {
    "id": 1
  }
}
```

---

## 5.5.2 Danh sách đánh giá dịch vụ
**GET** `/services/{serviceId}/reviews`

### Output
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "items": [
      {
        "id": 1,
        "bookingCode": "BK202605200001",
        "rating": 5,
        "comment": "Dịch vụ tốt"
      }
    ],
    "page": 1,
    "limit": 20,
    "total": 1
  }
}
```

---

# 5.6 User Management API

## 5.6.1 Danh sách người dùng
**GET** `/admin/users`

### Query
- `keyword`
- `role`
- `active`
- `page`
- `limit`

### Output
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "items": [
      {
        "id": 1,
        "name": "Admin",
        "phone": "0900000000",
        "email": "admin@gmail.com",
        "role": "ADMIN",
        "active": true
      }
    ],
    "page": 1,
    "limit": 20,
    "total": 1
  }
}
```

---

## 5.6.2 Cập nhật thông tin user
**PUT** `/admin/users/{id}`

### Input
```json
{
  "name": "Tech A",
  "phone": "0909999999",
  "email": "tech@gmail.com",
  "role": "TECHNICIAN",
  "active": true
}
```

### Output
```json
{
  "success": true,
  "message": "Cập nhật thành công",
  "data": null
}
```

---

## 5.6.3 Bật/tắt tài khoản
**POST** `/admin/users/{id}/active`

### Input
```json
{
  "active": false
}
```

### Output
```json
{
  "success": true,
  "message": "Cập nhật trạng thái thành công",
  "data": null
}
```

---

## 6. Gợi ý cấu trúc module code

```text
shop/
  src/main/java/vn/aeoc/ecom/
    controller/
    service/
    repository/
  src/main/java/vn/aeoc/entity/data/mysql/
    ...entities
```

---

## 7. Lưu ý triển khai sau này

- Có thể tách DTO request/response riêng cho từng API.
- Nên tạo enum cho `BookingStatus`.
- Nên log lịch sử trạng thái trong bảng riêng.
- Nên validate dữ liệu đầu vào bằng `jakarta.validation`.
- Nên phân quyền bằng `@IsUser`, `@IsAdmin`, `@IsSeller` theo mô hình hiện tại.

---

## 8. Danh sách công việc triển khai tiếp theo

1. Tạo entity cho các bảng mới.
2. Tạo repository kế thừa `AbsMysqlRepository`.
3. Tạo service kế thừa `BaseService`.
4. Tạo controller và mapping API.
5. Bổ sung validate, DTO, và xử lý nghiệp vụ.

