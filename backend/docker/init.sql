CREATE DATABASE IF NOT EXISTS techfix_auth;
CREATE DATABASE IF NOT EXISTS techfix_catalog;
CREATE DATABASE IF NOT EXISTS techfix_booking;

GRANT ALL PRIVILEGES ON techfix_auth.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON techfix_catalog.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON techfix_booking.* TO 'root'@'%';
FLUSH PRIVILEGES;

USE techfix_auth;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    full_name VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'ADMIN', 'TECHNICIAN') DEFAULT 'CUSTOMER',
    avatar_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Password is 'password' (bcrypt hash)

INSERT INTO users (email, password_hash, full_name, role, phone, created_at, updated_at) VALUES
('admin@techfix.pro', '$2b$10$iPm07F9ALCW.c9ofOCChpeQ9sdsSyimRGJ13E/N973GHg/VSoyrFa', 'Administrator', 'ADMIN', '0900000001', NOW(), NOW()),
('admin1@techfix.pro', '$2b$10$iPm07F9ALCW.c9ofOCChpeQ9sdsSyimRGJ13E/N973GHg/VSoyrFa', 'Administrator 2', 'ADMIN', '0900000002', NOW(), NOW()),
('tech@techfix.pro', '$2b$10$iPm07F9ALCW.c9ofOCChpeQ9sdsSyimRGJ13E/N973GHg/VSoyrFa', 'Technician 1', 'TECHNICIAN', '0900000003', NOW(), NOW()),
('tech1@techfix.pro', '$2b$10$iPm07F9ALCW.c9ofOCChpeQ9sdsSyimRGJ13E/N973GHg/VSoyrFa', 'Technician 2', 'TECHNICIAN', '0900000004', NOW(), NOW()),
('tech2@techfix.pro', '$2b$10$iPm07F9ALCW.c9ofOCChpeQ9sdsSyimRGJ13E/N973GHg/VSoyrFa', 'Technician 3', 'TECHNICIAN', '0900000005', NOW(), NOW()),
('tech3@techfix.pro', '$2b$10$iPm07F9ALCW.c9ofOCChpeQ9sdsSyimRGJ13E/N973GHg/VSoyrFa', 'Technician 4', 'TECHNICIAN', '0900000006', NOW(), NOW()),
('test@mail.com', '$2b$10$iPm07F9ALCW.c9ofOCChpeQ9sdsSyimRGJ13E/N973GHg/VSoyrFa', 'Vu LN', 'CUSTOMER', '0961887630', NOW(), NOW())
ON DUPLICATE KEY UPDATE email=email;

USE techfix_catalog;

-- Services Table (Already Exists in Script)
CREATE TABLE IF NOT EXISTS services (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    base_price DECIMAL(12, 2) NOT NULL,
    estimated_duration VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO services (id, name, description, base_price, estimated_duration) VALUES
('repair-hw', 'Sửa chữa Phần cứng', 'Chuyên trị các bệnh khó của Laptop', 300000, '2 hours'),
('upgrade-comp', 'Nâng cấp Linh kiện', 'Tăng tốc máy tính với SSD và RAM', 500000, '1 hour'),
('maintenance-gen', 'Vệ sinh & Bảo dưỡng', 'Quy trình 12 bước vệ sinh chuyên sâu', 150000, '45 mins'),
('software-install', 'Cài đặt Phần mềm', 'Cài đặt Windows, MacOS, Office', 100000, '30 mins')
ON DUPLICATE KEY UPDATE name=name;

-- Parts Table
CREATE TABLE IF NOT EXISTS parts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(12, 2) NOT NULL,
    stock INT DEFAULT 0,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO parts (name, description, price, stock) VALUES
('RAM 8GB DDR4', 'Kingston Fury Impact 3200MHz', 850000, 50),
('SSD 512GB NVMe', 'Samsung 970 EVO Plus', 1250000, 30),
('Màn hình Laptop 15.6 FHD', 'Tấm nền IPS chống chói', 2100000, 15),
('Bàn phím Dell XPS 15', 'Original Replacement', 450000, 20)
ON DUPLICATE KEY UPDATE name=name;

USE techfix_booking;
-- Bookings tables are auto-created by TypeORM usually, but we can seed if needed.
-- For now, verifying User and Catalog data is primary request.
