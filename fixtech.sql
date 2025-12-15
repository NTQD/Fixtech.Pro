-- =============================================
-- Database Schema for TechFix Pro Booking System
-- Engine: Microsoft SQL Server (T-SQL)
-- Converted by: Antigravity
-- =============================================

-- Create Database if not exists
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'TechFixProDB')
BEGIN
    CREATE DATABASE TechFixProDB;
END
GO

USE TechFixProDB;
GO

-- =============================================
-- A. USERS TABLE
-- Stores customer, technician, and admin information
-- =============================================
IF OBJECT_ID('dbo.users', 'U') IS NOT NULL DROP TABLE dbo.users;
CREATE TABLE users (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    email NVARCHAR(255) UNIQUE NOT NULL,
    phone NVARCHAR(20) UNIQUE,
    password_hash NVARCHAR(255), -- Nullable if using OAuth/Social Login later
    full_name NVARCHAR(100),
    role NVARCHAR(20) CHECK (role IN ('CUSTOMER', 'ADMIN', 'TECHNICIAN')) DEFAULT 'CUSTOMER',
    avatar_url NVARCHAR(MAX),
    created_at DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET(),
    updated_at DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET()
);
GO

-- =============================================
-- B. SERVICES TABLE
-- Stores available services formatted for the UI
-- =============================================
IF OBJECT_ID('dbo.services', 'U') IS NOT NULL DROP TABLE dbo.services;
CREATE TABLE services (
    id NVARCHAR(50) PRIMARY KEY, -- e.g., 'repair', 'upgrade', 'maintenance' or UUID
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    base_price DECIMAL(12, 2) NOT NULL, -- Storing money
    category NVARCHAR(50), -- 'Repair', 'Upgrade', 'Maintenance'
    is_active BIT DEFAULT 1, -- 1 = True, 0 = False
    image_url NVARCHAR(MAX),
    created_at DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET()
);
GO

-- =============================================
-- C. BOOKINGS TABLE
-- The core table linking Users and Services
-- =============================================
IF OBJECT_ID('dbo.bookings', 'U') IS NOT NULL DROP TABLE dbo.bookings;
CREATE TABLE bookings (
    id NVARCHAR(20) PRIMARY KEY, -- e.g., '#ORD-123456' manually generated or sequence
    user_id UNIQUEIDENTIFIER REFERENCES users(id) ON DELETE SET NULL, -- Link to registered user (optional)
    
    -- Customer Info Snapshot (In case user is not registered or updates profile later)
    customer_name NVARCHAR(100) NOT NULL,
    customer_phone NVARCHAR(20) NOT NULL,
    customer_email NVARCHAR(255),
    
    -- Device Information (Stored as JSON text for flexibility)
    -- Structure: { "type": "Laptop", "brand": "Dell", "model": "XPS 15", "serial": "..." }
    device_info NVARCHAR(MAX), 
    
    issue_description NVARCHAR(MAX),
    
    -- Scheduling
    scheduled_date DATE NOT NULL,
    scheduled_time TIME NOT NULL,
    
    -- Status Tracking
    status NVARCHAR(20) CHECK (status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')) DEFAULT 'PENDING',
    
    total_amount DECIMAL(12, 2),
    created_at DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET(),
    updated_at DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET()
);
GO

-- =============================================
-- D. BOOKING ITEMS TABLE
-- Handles many-to-many relationship: One Booking can have multiple Services
-- =============================================
IF OBJECT_ID('dbo.booking_items', 'U') IS NOT NULL DROP TABLE dbo.booking_items;
CREATE TABLE booking_items (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    booking_id NVARCHAR(20) REFERENCES bookings(id) ON DELETE CASCADE,
    service_id NVARCHAR(50) REFERENCES services(id),
    
    -- Snaphots price at time of booking to prevent historical data changes when service price changes
    price_at_booking DECIMAL(12, 2) NOT NULL, 
    quantity INT DEFAULT 1
);
GO

-- =============================================
-- SAMPLE DATA (SEEDING)
-- =============================================

-- 1. Insert Sample Services
INSERT INTO services (id, title, description, category, base_price) VALUES
('repair-hw', N'Sửa chữa Phần cứng', N'Khắc phục lỗi nguồn, mainboard...', N'Sửa chữa', 300000),
('upgrade-ram', N'Nâng cấp RAM', N'Nâng cấp bộ nhớ RAM chính hãng', N'Nâng cấp', 500000),
('maintenance-pro', N'Vệ sinh chuyên sâu', N'Vệ sinh 12 bước, tra keo MX-4', N'Bảo dưỡng', 150000);
GO

-- 2. Insert Sample Users
INSERT INTO users (email, phone, full_name, role) VALUES
('admin@techfix.com', '0901234567', N'Quản Trị Viên', 'ADMIN'),
('khachhang@gmail.com', '0912345678', N'Nguyễn Văn A', 'CUSTOMER');
GO

-- 3. Insert Sample Booking
-- Order #ORD-2023001: Mr. A books a Maintenance service
-- First, get the User ID (Variables are needed in T-SQL for dynamic UUID retrieval or just subquery)
DECLARE @UserId UNIQUEIDENTIFIER;
SELECT @UserId = id FROM users WHERE email = 'khachhang@gmail.com';

INSERT INTO bookings (id, user_id, customer_name, customer_phone, device_info, issue_description, scheduled_date, scheduled_time, status, total_amount)
VALUES (
    '#ORD-2023001', 
    @UserId,
    N'Nguyễn Văn A', 
    '0912345678',
    N'{"type": "Laptop", "brand": "Dell", "model": "Inspiron 5500"}',
    N'Máy chạy nóng, quạt kêu to',
    '2023-12-25',
    '10:00:00',
    'PENDING',
    150000
);

-- Link the Service to the Booking
INSERT INTO booking_items (booking_id, service_id, price_at_booking)
VALUES ('#ORD-2023001', 'maintenance-pro', 150000);
GO

-- =============================================
-- QUERIES FOR VERIFICATION
-- =============================================
-- Get all bookings with customer name and service details
/*
SELECT 
    b.id as booking_code,
    b.customer_name,
    b.scheduled_date,
    b.status,
    s.title as service_name,
    bi.price_at_booking
FROM bookings b
JOIN booking_items bi ON b.id = bi.booking_id
JOIN services s ON bi.service_id = s.id;
*/
