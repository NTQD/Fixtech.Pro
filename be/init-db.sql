-- MySQL dump 10.13  Distrib 8.1.0, for Linux (x86_64)
--
-- Host: localhost    Database: ecom
-- ------------------------------------------------------
-- Server version	8.1.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `ecom`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `ecom` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `ecom`;

--
-- Table structure for table `repair_booking`
--

DROP TABLE IF EXISTS `repair_booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repair_booking` (
  `id` int NOT NULL AUTO_INCREMENT,
  `booking_code` varchar(50) NOT NULL,
  `customer_id` int DEFAULT NULL,
  `customer_name` varchar(200) NOT NULL,
  `customer_phone` varchar(15) NOT NULL,
  `customer_email` varchar(255) DEFAULT NULL,
  `device_type` varchar(200) DEFAULT NULL,
  `device_brand` varchar(100) DEFAULT NULL,
  `device_model` varchar(100) DEFAULT NULL,
  `issue_description` text,
  `preferred_date` date DEFAULT NULL,
  `preferred_time_slot` varchar(50) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `note` varchar(1000) DEFAULT NULL,
  `status` varchar(40) NOT NULL,
  `technician_id` int DEFAULT NULL,
  `total_estimated_price` bigint DEFAULT '0',
  `total_estimated_minutes` int DEFAULT '0',
  `scheduled_at` datetime DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_repair_booking_code` (`booking_code`),
  KEY `idx_repair_booking_customer_id` (`customer_id`),
  KEY `idx_repair_booking_status` (`status`),
  KEY `idx_repair_booking_technician_id` (`technician_id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repair_booking`
--

LOCK TABLES `repair_booking` WRITE;
/*!40000 ALTER TABLE `repair_booking` DISABLE KEYS */;
INSERT INTO `repair_booking` VALUES (1,'BK000001',7,'Khánh Trần','069478213','khanh@mail.com','Laptop - Lenovo Thinkpad E14 Gen 7 ()',NULL,NULL,'cần cài lại win bản quyền và các phần mềm Office \nNotes: ','2025-12-19','16:00:00',NULL,NULL,'COMPLETED',3,100000,0,NULL,'2025-12-23 15:02:53','2025-12-23 15:05:56'),(2,'BK000002',8,'Vu Ln','0635987421','vu@mail.com','Laptop - Asus  TUF Gaming F16 ()',NULL,NULL,'cần nâng ram 32 \nNotes: ','2025-12-20','00:00:00',NULL,NULL,'IN_PROGRESS',4,4130000,0,NULL,'2025-12-23 15:09:24','2025-12-23 15:30:43'),(3,'BK000003',9,'Châu','0964587132','chau@mail.com','Laptop - Dell XPS 15 ()',NULL,NULL,'máy bẩn \nNotes: ','2025-12-18','14:14:00',NULL,NULL,'COMPLETED',5,200000,0,NULL,'2025-12-23 15:12:24','2025-12-30 17:33:58'),(4,'BK000004',10,'Ph Anh','0526143978','phanh@mail.com','Laptop - Asus  ExpertBook P5 ()',NULL,NULL,'máy hỏng màn rồi \nNotes: ','2025-12-22','11:11:00',NULL,NULL,'COMPLETED',3,4060000,0,NULL,'2025-12-23 15:17:20','2025-12-27 07:36:32'),(5,'BK000005',11,'Tuấn','0682149711','tuan@mail.com','Laptop - HP Omen 16 ()',NULL,NULL,'hỏng bàn phím \nNotes: ','2025-12-23','14:22:00',NULL,NULL,'IN_PROGRESS',6,300000,0,NULL,'2025-12-23 15:20:16','2025-12-27 13:11:33'),(6,'BK000006',12,'Trường Lee','0963824517','truong@mail.com','Laptop - Acer Nitro 5 ()',NULL,NULL,'nâng ổ cứng \nNotes: ','2025-12-24','15:15:00',NULL,NULL,'IN_PROGRESS',4,500000,0,NULL,'2025-12-23 15:22:56','2025-12-27 13:25:02'),(7,'BK000007',10,'Ương Ương','0693152478','phanh@mail.com','Laptop - Lenovo Thinkpad E14 ()',NULL,NULL,'máy bẩn cần vệ sinh , tra keo tản nhiệt \nNotes: ','2025-12-24','15:00:00',NULL,NULL,'IN_PROGRESS',3,150000,0,NULL,'2025-12-24 05:17:59','2025-12-27 13:11:34'),(8,'BK000008',7,'Thẩm Ấu Sở','0263514798','test@mail.com','Laptop - Macbook Pro M4 ()',NULL,NULL,'hỏng bàn phím \nNotes: ','2025-12-24','14:22:00',NULL,'Máy bị nước vào, chập cháy IC Nguồn và rỉ sét socket màn hình. Cần thay IC mới.','PENDING',3,300000,0,NULL,'2025-12-24 05:20:54','2026-03-19 15:19:24'),(9,'BK000009',8,'Tần Dĩ Dĩ','0693852471','vu@mail.','Laptop - Macbook Air M2 ()',NULL,NULL,'Hỏng loa \nNotes: ','2025-12-26','14:24:00',NULL,NULL,'COMPLETED',5,4890000,0,NULL,'2025-12-26 15:25:43','2025-12-29 16:46:30'),(10,'BK000010',12,'Lý Trường Thanh','0965382147','truong@mail.com','Laptop - MSI Katana 15 ()',NULL,NULL,'nâng cấp ổ cứng \nNotes: ','2025-12-10','11:33:00',NULL,NULL,'CONFIRMED',3,3370000,0,NULL,'2025-12-26 15:32:49','2025-12-31 15:42:46'),(11,'BK000011',12,'Bùi Hiếu','0695478321','hieu@mail.com','Laptop - Lenovo legion slim 3 ()',NULL,NULL,'máy chậm \nNotes: ','2025-12-27','14:12:00',NULL,NULL,'CONFIRMED',6,150000,0,NULL,'2025-12-27 07:13:29','2025-12-27 07:15:17'),(12,'BK000012',7,'Trần Trung','0965321487','test@mai.com','Laptop - Dell máy của thầy ()',NULL,NULL,'vệ sinh \nNotes: ','2025-12-27','14:32:00',NULL,NULL,'IN_PROGRESS',5,200000,0,NULL,'2025-12-27 07:32:57','2025-12-30 17:44:17'),(13,'BK000013',8,'Hoshino Ai','0968532714','vu@mail.com','Laptop - Macbook Pro 14 M5 ()',NULL,NULL,'máy bẩn \nNotes: ','2025-12-31','14:00:00',NULL,NULL,'IN_PROGRESS',3,150000,0,NULL,'2025-12-27 13:01:09','2025-12-29 16:46:07'),(14,'BK000014',9,'Emilia','0968573124','chau@mail.com','Laptop - Dell Inspiron 15 ()',NULL,NULL,'Cần nâng ram \nNotes: ','2025-12-31','14:00:00',NULL,NULL,'PENDING',4,500000,0,NULL,'2025-12-27 13:03:47','2025-12-27 13:03:46'),(15,'BK000015',10,'Kaoruko Waguri','0869315247','phanh@mail.com','Laptop - Asus Zenbook S 16 ()',NULL,NULL,'hỏng bán phím \nNotes: ','2025-12-31','14:00:00',NULL,NULL,'PENDING',5,300000,0,NULL,'2025-12-27 13:06:40','2025-12-27 13:06:40'),(16,'BK000016',11,'Ainz Ooal Gown','0635298147','tuan@mail.com','Laptop - Hp Victus 16 ()',NULL,NULL,'Cần cài lại Win \nNotes: ','2025-12-31','14:00:00',NULL,NULL,'PENDING',6,100000,0,NULL,'2025-12-27 13:10:13','2025-12-27 13:10:13'),(17,'BK000017',12,'Trần Hán Thăng','0639254871','truong@mail.com','Laptop - MSI Katana A15 ()',NULL,NULL,'hỏng Touchpad \nNotes: ','2025-12-31','14:30:00',NULL,NULL,'PENDING',6,300000,0,NULL,'2025-12-27 13:16:40','2025-12-27 13:16:39'),(18,'BK000018',11,'Shiina Mahiru','0526931487','tuan@mail.com','Laptop - Hp Probook 440 ()',NULL,NULL,'hỏng web cam \nNotes: ','2025-12-31','14:45:00',NULL,NULL,'IN_PROGRESS',3,1020000,0,NULL,'2025-12-30 16:11:54','2025-12-31 15:21:50'),(19,'BK000019',10,'Hoshino Ruby','0693582741','phanh@mail.com','Laptop - Lenovo IdeaPad Slim 5 OLED ()',NULL,NULL,'tra keo tản nhiệt, vệ sinh máy \nNotes: ','2025-12-30','14:50:00',NULL,NULL,'COMPLETED',3,200000,0,NULL,'2025-12-30 16:51:22','2026-01-07 08:31:45'),(20,'BK000020',7,'Thẩm Lãng','0536921478','test@mail.com','Laptop - Macbook Air M4 ()',NULL,NULL,'hỏng loa \nNotes: ','2026-01-07','14:00:00',NULL,NULL,'IN_PROGRESS',3,4890000,0,NULL,'2026-01-06 06:58:14','2026-01-06 07:01:03'),(21,'BK000021',13,'vu','0369258741','test@mail.com','Laptop - macbook pro m4 ()',NULL,NULL,'hỏng màn hình\n \nNotes: ','2025-12-31','17:00:00',NULL,NULL,'PENDING',4,300000,0,NULL,'2026-01-07 08:30:30','2026-01-07 08:30:30'),(22,'BK000022',7,'Vu leee','0362598741','test@mail.com','Laptop - macbook macbook air m5 ()',NULL,NULL,'máy bẩn cần vệ sinh \nNotes: ','2026-03-20','10:50:00',NULL,NULL,'COMPLETED',3,150000,0,NULL,'2026-03-18 15:51:53','2026-03-19 14:51:23'),(23,'BK000023',7,'Như Vũ','0693542178','test@mail.com','Laptop - Macbook macbook air M4 ()',NULL,NULL,'máy bẩn cần vệ sinh \nNotes: ','2026-03-20','10:00:00',NULL,NULL,'CANCELLED',3,150000,0,NULL,'2026-03-18 16:04:33','2026-03-19 14:24:50'),(24,'BK000024',8,'Cố Thanh Sơn','0263514987','vu@mail.com','Laptop - Lenovo Thinkbook 14 G7+ ()',NULL,NULL,'nâng cấp ram \nNotes: ','2026-03-21','14:14:00',NULL,'đang thay thế ram','COMPLETED',3,500000,0,NULL,'2026-03-20 14:22:17','2026-03-24 06:21:58'),(25,'BK000025',7,'Tạ Đạo Linh','0263514798','test@mail.com','Laptop - Lenovo ThinkPad x1 carbon gen 9 ()',NULL,NULL,'cần thay bàn phím \nNotes: ','2026-03-21','14:50:00',NULL,NULL,'PENDING',4,300000,0,NULL,'2026-03-20 14:41:47','2026-03-20 14:41:47'),(26,'BK000026',7,'Tạ Đạo Linh','0263514798','test@mail.com','Laptop - Lenovo ThinkPad x1 carbon gen 9 ()',NULL,NULL,'cần thay bàn phím \nNotes: ','2026-03-21','14:50:00',NULL,NULL,'PENDING',5,300000,0,NULL,'2026-03-20 14:50:10','2026-03-20 14:50:09'),(27,'BK000027',7,'Tạ Đạo Linh','0263965314','test@mail.com','Laptop - Lenovo ThinkPad x1 carbon gen 9 ()',NULL,NULL,'cần thay bàn phím \nNotes: ','2026-03-21','14:50:00',NULL,NULL,'PENDING',5,300000,0,NULL,'2026-03-20 14:57:25','2026-03-23 09:06:07'),(28,'BK000028',8,'Tô Tuyết Nhi','0263514798','test@mail.com','Laptop - Lenovo ThinkPad x1 carbon gen 9 ()',NULL,NULL,'cần thay bàn loa \nNotes: ','2026-03-25','14:50:00',NULL,NULL,'IN_PROGRESS',3,6820000,0,NULL,'2026-03-20 14:59:31','2026-03-23 17:25:33'),(29,'BK000029',9,'Hứa Thất An','0263514798','test@mail.com','Laptop - Lenovo ThinkPad x1 carbon gen 9 ()',NULL,NULL,'cần thay bàn pin \nNotes: ','2026-03-26','14:50:00',NULL,'','COMPLETED',3,300000,0,NULL,'2026-03-20 15:41:35','2026-03-21 14:17:04'),(30,'BK000030',7,'Phi Nguyệt','0163254798','test@mail.com','Laptop - Lenovo ThinkPad x1 carbon gen 9 ()',NULL,NULL,'cần thay loa máy tính \nNotes: ','2026-03-28','14:50:00',NULL,NULL,'COMPLETED',3,300000,0,NULL,'2026-03-21 14:26:32','2026-03-21 14:26:50'),(31,'BK000031',7,'Phi Nguyệt','0163254798','test@mail.com','Laptop - Lenovo ThinkPad x1 carbon gen 9 ()',NULL,NULL,'cần thay loa máy tính \nNotes: ','2026-03-28','14:50:00',NULL,NULL,'COMPLETED',6,300000,0,NULL,'2026-03-24 16:09:07','2026-03-25 09:58:21'),(32,'BK000032',7,'Phi Nguyệt','0163254798','test@mail.com','Laptop - Lenovo ThinkPad x1 carbon gen 9 ()',NULL,NULL,'cần thay loa máy tính \nNotes: ','2026-03-28','14:50:00',NULL,NULL,'COMPLETED',3,300000,0,NULL,'2026-03-24 17:06:53','2026-03-24 17:07:11'),(33,'BK000033',7,'Phi Nguyệt','0163254798','test@mail.com','Laptop - Lenovo ThinkPad x1 carbon gen 9 ()',NULL,NULL,'cần thay loa máy tính \nNotes: ','2026-03-28','14:50:00',NULL,NULL,'PENDING',3,300000,0,NULL,'2026-03-25 09:43:24','2026-03-25 09:43:23'),(34,'BK000034',7,'Phi Nguyệt','0163254798','test@mail.com','Laptop - Lenovo ThinkPad x1 carbon gen 9 ()',NULL,NULL,'cần thay loa máy tính \nNotes: ','2026-03-28','14:50:00',NULL,NULL,'COMPLETED',4,300000,0,NULL,'2026-03-25 09:44:01','2026-03-25 09:44:22'),(35,'BK000035',7,'Vũ','02369852147','test@mail.com','Laptop - asus vivobook ()',NULL,NULL,'máy bẩn \nNotes: ','2026-04-16','10:42:00',NULL,NULL,'PENDING',3,150000,0,NULL,'2026-04-06 01:29:31','2026-04-06 01:29:31');
/*!40000 ALTER TABLE `repair_booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repair_booking_item`
--

DROP TABLE IF EXISTS `repair_booking_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repair_booking_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `booking_id` int NOT NULL,
  `service_id` int NOT NULL,
  `service_name` varchar(255) NOT NULL,
  `quantity` int DEFAULT '1',
  `estimated_price` bigint DEFAULT '0',
  `final_price` bigint DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_repair_booking_item_booking_id` (`booking_id`),
  KEY `idx_repair_booking_item_service_id` (`service_id`),
  CONSTRAINT `fk_repair_booking_item_booking` FOREIGN KEY (`booking_id`) REFERENCES `repair_booking` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_repair_booking_item_service` FOREIGN KEY (`service_id`) REFERENCES `repair_service` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repair_booking_item`
--

LOCK TABLES `repair_booking_item` WRITE;
/*!40000 ALTER TABLE `repair_booking_item` DISABLE KEYS */;
INSERT INTO `repair_booking_item` VALUES (1,1,3,'Cài đặt Phần mềm',1,100000,100000),(2,2,4,'Nâng cấp Linh kiện',1,500000,500000),(3,3,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(4,4,2,'Sửa chữa Phần cứng',1,300000,300000),(5,5,2,'Sửa chữa Phần cứng',1,300000,300000),(6,6,4,'Nâng cấp Linh kiện',1,500000,500000),(7,7,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(8,8,2,'Sửa chữa Phần cứng',1,300000,300000),(9,9,2,'Sửa chữa Phần cứng',1,300000,300000),(10,10,4,'Nâng cấp Linh kiện',1,500000,500000),(11,11,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(12,12,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(13,13,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(14,14,4,'Nâng cấp Linh kiện',1,500000,500000),(15,15,2,'Sửa chữa Phần cứng',1,300000,300000),(16,16,3,'Cài đặt Phần mềm',1,100000,100000),(17,17,2,'Sửa chữa Phần cứng',1,300000,300000),(18,18,2,'Sửa chữa Phần cứng',1,300000,300000),(19,18,3,'Cài đặt Phần mềm',1,100000,100000),(20,19,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(21,20,2,'Sửa chữa Phần cứng',1,300000,300000),(22,21,2,'Sửa chữa Phần cứng',1,300000,300000),(23,22,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(24,23,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(25,24,4,'Nâng cấp Linh kiện',1,500000,500000),(26,25,2,'Sửa chữa Phần cứng',1,300000,300000),(27,26,2,'Sửa chữa Phần cứng',1,300000,300000),(28,27,2,'Sửa chữa Phần cứng',1,300000,300000),(29,28,2,'Sửa chữa Phần cứng',1,300000,300000),(30,29,2,'Sửa chữa Phần cứng',1,300000,300000),(31,30,2,'Sửa chữa Phần cứng',1,300000,300000),(32,31,2,'Sửa chữa Phần cứng',1,300000,300000),(33,32,2,'Sửa chữa Phần cứng',1,300000,300000),(34,33,2,'Sửa chữa Phần cứng',1,300000,300000),(35,34,2,'Sửa chữa Phần cứng',1,300000,300000),(36,35,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(37,1,3,'Cài đặt Phần mềm',1,100000,100000),(38,2,4,'Nâng cấp Linh kiện',1,500000,500000),(39,3,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(40,4,2,'Sửa chữa Phần cứng',1,300000,300000),(41,5,2,'Sửa chữa Phần cứng',1,300000,300000),(42,6,4,'Nâng cấp Linh kiện',1,500000,500000),(43,7,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(44,8,2,'Sửa chữa Phần cứng',1,300000,300000),(45,9,2,'Sửa chữa Phần cứng',1,300000,300000),(46,10,4,'Nâng cấp Linh kiện',1,500000,500000),(47,11,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(48,12,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(49,13,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(50,14,4,'Nâng cấp Linh kiện',1,500000,500000),(51,15,2,'Sửa chữa Phần cứng',1,300000,300000),(52,16,3,'Cài đặt Phần mềm',1,100000,100000),(53,17,2,'Sửa chữa Phần cứng',1,300000,300000),(54,18,2,'Sửa chữa Phần cứng',1,300000,300000),(55,18,3,'Cài đặt Phần mềm',1,100000,100000),(56,19,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(57,20,2,'Sửa chữa Phần cứng',1,300000,300000),(58,21,2,'Sửa chữa Phần cứng',1,300000,300000),(59,22,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(60,23,1,'Vệ sinh & Bảo dưỡng',1,150000,150000),(61,24,4,'Nâng cấp Linh kiện',1,500000,500000),(62,25,2,'Sửa chữa Phần cứng',1,300000,300000),(63,26,2,'Sửa chữa Phần cứng',1,300000,300000),(64,27,2,'Sửa chữa Phần cứng',1,300000,300000),(65,28,2,'Sửa chữa Phần cứng',1,300000,300000),(66,29,2,'Sửa chữa Phần cứng',1,300000,300000),(67,30,2,'Sửa chữa Phần cứng',1,300000,300000),(68,31,2,'Sửa chữa Phần cứng',1,300000,300000),(69,32,2,'Sửa chữa Phần cứng',1,300000,300000),(70,33,2,'Sửa chữa Phần cứng',1,300000,300000),(71,34,2,'Sửa chữa Phần cứng',1,300000,300000),(72,35,1,'Vệ sinh & Bảo dưỡng',1,150000,150000);
/*!40000 ALTER TABLE `repair_booking_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repair_booking_note`
--

DROP TABLE IF EXISTS `repair_booking_note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repair_booking_note` (
  `id` int NOT NULL AUTO_INCREMENT,
  `booking_id` int NOT NULL,
  `note` varchar(1000) NOT NULL,
  `created_by` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_repair_booking_note_booking_id` (`booking_id`),
  CONSTRAINT `fk_repair_booking_note_booking` FOREIGN KEY (`booking_id`) REFERENCES `repair_booking` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repair_booking_note`
--

LOCK TABLES `repair_booking_note` WRITE;
/*!40000 ALTER TABLE `repair_booking_note` DISABLE KEYS */;
INSERT INTO `repair_booking_note` VALUES (1,1,'cần cài lại win bản quyền và các phần mềm Office \nNotes: ',7,'2025-12-23 15:02:53'),(2,2,'cần nâng ram 32 \nNotes: ',8,'2025-12-23 15:09:24'),(3,3,'máy bẩn \nNotes: ',9,'2025-12-23 15:12:24'),(4,4,'máy hỏng màn rồi \nNotes: ',10,'2025-12-23 15:17:20'),(5,5,'hỏng bàn phím \nNotes: ',11,'2025-12-23 15:20:16'),(6,6,'nâng ổ cứng \nNotes: ',12,'2025-12-23 15:22:56'),(7,7,'máy bẩn cần vệ sinh , tra keo tản nhiệt \nNotes: ',10,'2025-12-24 05:17:59'),(8,8,'hỏng bàn phím \nNotes: ',7,'2025-12-24 05:20:54'),(9,9,'Hỏng loa \nNotes: ',8,'2025-12-26 15:25:43'),(10,10,'nâng cấp ổ cứng \nNotes: ',12,'2025-12-26 15:32:49'),(11,11,'máy chậm \nNotes: ',12,'2025-12-27 07:13:29'),(12,12,'vệ sinh \nNotes: ',7,'2025-12-27 07:32:57'),(13,13,'máy bẩn \nNotes: ',8,'2025-12-27 13:01:09'),(14,14,'Cần nâng ram \nNotes: ',9,'2025-12-27 13:03:47'),(15,15,'hỏng bán phím \nNotes: ',10,'2025-12-27 13:06:40'),(16,16,'Cần cài lại Win \nNotes: ',11,'2025-12-27 13:10:13'),(17,17,'hỏng Touchpad \nNotes: ',12,'2025-12-27 13:16:40'),(18,18,'hỏng web cam \nNotes: ',11,'2025-12-30 16:11:54'),(19,19,'tra keo tản nhiệt, vệ sinh máy \nNotes: ',10,'2025-12-30 16:51:22'),(20,20,'hỏng loa \nNotes: ',7,'2026-01-06 06:58:14'),(21,21,'hỏng màn hình\n \nNotes: ',13,'2026-01-07 08:30:30'),(22,22,'máy bẩn cần vệ sinh \nNotes: ',7,'2026-03-18 15:51:53'),(23,23,'máy bẩn cần vệ sinh \nNotes: ',7,'2026-03-18 16:04:33'),(24,24,'nâng cấp ram \nNotes: ',8,'2026-03-20 14:22:17'),(25,25,'cần thay bàn phím \nNotes: ',7,'2026-03-20 14:41:47'),(26,26,'cần thay bàn phím \nNotes: ',7,'2026-03-20 14:50:10'),(27,27,'cần thay bàn phím \nNotes: ',7,'2026-03-20 14:57:25'),(28,28,'cần thay bàn loa \nNotes: ',8,'2026-03-20 14:59:31'),(29,29,'cần thay bàn pin \nNotes: ',9,'2026-03-20 15:41:35'),(30,30,'cần thay loa máy tính \nNotes: ',7,'2026-03-21 14:26:32'),(31,31,'cần thay loa máy tính \nNotes: ',7,'2026-03-24 16:09:07'),(32,32,'cần thay loa máy tính \nNotes: ',7,'2026-03-24 17:06:53'),(33,33,'cần thay loa máy tính \nNotes: ',7,'2026-03-25 09:43:24'),(34,34,'cần thay loa máy tính \nNotes: ',7,'2026-03-25 09:44:01'),(35,35,'máy bẩn \nNotes: ',7,'2026-04-06 01:29:31');
/*!40000 ALTER TABLE `repair_booking_note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repair_booking_status_history`
--

DROP TABLE IF EXISTS `repair_booking_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repair_booking_status_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `booking_id` int NOT NULL,
  `from_status` varchar(40) DEFAULT NULL,
  `to_status` varchar(40) NOT NULL,
  `note` varchar(1000) DEFAULT NULL,
  `changed_by` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_repair_booking_status_history_booking_id` (`booking_id`),
  CONSTRAINT `fk_repair_booking_status_history_booking` FOREIGN KEY (`booking_id`) REFERENCES `repair_booking` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repair_booking_status_history`
--

LOCK TABLES `repair_booking_status_history` WRITE;
/*!40000 ALTER TABLE `repair_booking_status_history` DISABLE KEYS */;
INSERT INTO `repair_booking_status_history` VALUES (1,1,NULL,'PENDING','Khách hàng tạo đơn',7,'2025-12-23 15:02:53'),(2,3,NULL,'PENDING','Khách hàng tạo đơn',9,'2025-12-23 15:12:24'),(3,4,NULL,'PENDING','Khách hàng tạo đơn',10,'2025-12-23 15:17:20'),(4,9,NULL,'PENDING','Khách hàng tạo đơn',8,'2025-12-26 15:25:43'),(5,19,NULL,'PENDING','Khách hàng tạo đơn',10,'2025-12-30 16:51:22'),(6,22,NULL,'PENDING','Khách hàng tạo đơn',7,'2026-03-18 15:51:53'),(7,24,NULL,'PENDING','Khách hàng tạo đơn',8,'2026-03-20 14:22:17'),(8,29,NULL,'PENDING','Khách hàng tạo đơn',9,'2026-03-20 15:41:35'),(9,30,NULL,'PENDING','Khách hàng tạo đơn',7,'2026-03-21 14:26:32'),(10,31,NULL,'PENDING','Khách hàng tạo đơn',7,'2026-03-24 16:09:07'),(11,32,NULL,'PENDING','Khách hàng tạo đơn',7,'2026-03-24 17:06:53'),(12,34,NULL,'PENDING','Khách hàng tạo đơn',7,'2026-03-25 09:44:01'),(16,1,'PENDING','CONFIRMED','Đã xác nhận lịch',3,'2025-12-23 16:02:53'),(17,3,'PENDING','CONFIRMED','Đã xác nhận lịch',5,'2025-12-23 16:12:24'),(18,4,'PENDING','CONFIRMED','Đã xác nhận lịch',3,'2025-12-23 16:17:20'),(19,9,'PENDING','CONFIRMED','Đã xác nhận lịch',5,'2025-12-26 16:25:43'),(20,19,'PENDING','CONFIRMED','Đã xác nhận lịch',3,'2025-12-30 17:51:22'),(21,22,'PENDING','CONFIRMED','Đã xác nhận lịch',3,'2026-03-18 16:51:53'),(22,24,'PENDING','CONFIRMED','Đã xác nhận lịch',3,'2026-03-20 15:22:17'),(23,29,'PENDING','CONFIRMED','Đã xác nhận lịch',3,'2026-03-20 16:41:35'),(24,30,'PENDING','CONFIRMED','Đã xác nhận lịch',3,'2026-03-21 15:26:32'),(25,31,'PENDING','CONFIRMED','Đã xác nhận lịch',6,'2026-03-24 17:09:07'),(26,32,'PENDING','CONFIRMED','Đã xác nhận lịch',3,'2026-03-24 18:06:53'),(27,34,'PENDING','CONFIRMED','Đã xác nhận lịch',4,'2026-03-25 10:44:01'),(31,1,'CONFIRMED','IN_PROGRESS','Thợ đang xử lý',3,'2025-12-24 15:02:53'),(32,3,'CONFIRMED','IN_PROGRESS','Thợ đang xử lý',5,'2025-12-24 15:12:24'),(33,4,'CONFIRMED','IN_PROGRESS','Thợ đang xử lý',3,'2025-12-24 15:17:20'),(34,9,'CONFIRMED','IN_PROGRESS','Thợ đang xử lý',5,'2025-12-27 15:25:43'),(35,19,'CONFIRMED','IN_PROGRESS','Thợ đang xử lý',3,'2025-12-31 16:51:22'),(36,22,'CONFIRMED','IN_PROGRESS','Thợ đang xử lý',3,'2026-03-19 15:51:53'),(37,24,'CONFIRMED','IN_PROGRESS','Thợ đang xử lý',3,'2026-03-21 14:22:17'),(38,29,'CONFIRMED','IN_PROGRESS','Thợ đang xử lý',3,'2026-03-21 15:41:35'),(39,30,'CONFIRMED','IN_PROGRESS','Thợ đang xử lý',3,'2026-03-22 14:26:32'),(40,31,'CONFIRMED','IN_PROGRESS','Thợ đang xử lý',6,'2026-03-25 16:09:07'),(41,32,'CONFIRMED','IN_PROGRESS','Thợ đang xử lý',3,'2026-03-25 17:06:53'),(42,34,'CONFIRMED','IN_PROGRESS','Thợ đang xử lý',4,'2026-03-26 09:44:01'),(46,1,'IN_PROGRESS','COMPLETED','Hoàn thành sửa chữa',3,'2025-12-25 15:02:53'),(47,3,'IN_PROGRESS','COMPLETED','Hoàn thành sửa chữa',5,'2025-12-25 15:12:24'),(48,4,'IN_PROGRESS','COMPLETED','Hoàn thành sửa chữa',3,'2025-12-25 15:17:20'),(49,9,'IN_PROGRESS','COMPLETED','Hoàn thành sửa chữa',5,'2025-12-28 15:25:43'),(50,19,'IN_PROGRESS','COMPLETED','Hoàn thành sửa chữa',3,'2026-01-01 16:51:22'),(51,22,'IN_PROGRESS','COMPLETED','Hoàn thành sửa chữa',3,'2026-03-20 15:51:53'),(52,24,'IN_PROGRESS','COMPLETED','Hoàn thành sửa chữa',3,'2026-03-22 14:22:17'),(53,29,'IN_PROGRESS','COMPLETED','Hoàn thành sửa chữa',3,'2026-03-22 15:41:35'),(54,30,'IN_PROGRESS','COMPLETED','Hoàn thành sửa chữa',3,'2026-03-23 14:26:32'),(55,31,'IN_PROGRESS','COMPLETED','Hoàn thành sửa chữa',6,'2026-03-26 16:09:07'),(56,32,'IN_PROGRESS','COMPLETED','Hoàn thành sửa chữa',3,'2026-03-26 17:06:53'),(57,34,'IN_PROGRESS','COMPLETED','Hoàn thành sửa chữa',4,'2026-03-27 09:44:01');
/*!40000 ALTER TABLE `repair_booking_status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repair_review`
--

DROP TABLE IF EXISTS `repair_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repair_review` (
  `id` int NOT NULL AUTO_INCREMENT,
  `booking_id` int NOT NULL,
  `service_id` int NOT NULL,
  `customer_id` int DEFAULT NULL,
  `rating` int NOT NULL,
  `comment` varchar(1000) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_repair_review_service_id` (`service_id`),
  KEY `idx_repair_review_booking_id` (`booking_id`),
  CONSTRAINT `fk_repair_review_booking` FOREIGN KEY (`booking_id`) REFERENCES `repair_booking` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_repair_review_service` FOREIGN KEY (`service_id`) REFERENCES `repair_service` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repair_review`
--

LOCK TABLES `repair_review` WRITE;
/*!40000 ALTER TABLE `repair_review` DISABLE KEYS */;
INSERT INTO `repair_review` VALUES (1,1,3,7,4,'Tôi rất hài lòng với thái độ phục vụ của Fixtech.','2025-12-24 15:05:56'),(2,1,3,7,4,'Dịch vụ rất tốt, thợ nhiệt tình, sửa nhanh gọn.','2025-12-24 15:05:56'),(3,3,1,9,5,'Chất lượng tuyệt vời, giá cả hợp lý.','2025-12-31 17:33:58'),(4,3,1,9,5,'Sửa chữa có tâm, máy hoạt động hoàn hảo.','2025-12-31 17:33:58'),(5,4,2,10,5,'Dịch vụ rất tốt, thợ nhiệt tình, sửa nhanh gọn.','2025-12-28 07:36:32'),(6,4,2,10,4,'Dịch vụ rất tốt, thợ nhiệt tình, sửa nhanh gọn.','2025-12-28 07:36:32'),(7,9,2,8,4,'Thợ đến đúng giờ, máy chạy êm ru sau khi sửa.','2025-12-30 16:46:30'),(8,9,2,8,5,'Sửa chữa có tâm, máy hoạt động hoàn hảo.','2025-12-30 16:46:30'),(9,19,1,10,5,'Dịch vụ rất tốt, thợ nhiệt tình, sửa nhanh gọn.','2026-01-08 08:31:45'),(10,19,1,10,5,'Chất lượng tuyệt vời, giá cả hợp lý.','2026-01-08 08:31:45'),(11,22,1,7,4,'Sửa chữa có tâm, máy hoạt động hoàn hảo.','2026-03-20 14:51:23'),(12,22,1,7,4,'Dịch vụ rất tốt, thợ nhiệt tình, sửa nhanh gọn.','2026-03-20 14:51:23'),(13,24,4,8,5,'Chất lượng tuyệt vời, giá cả hợp lý.','2026-03-25 06:21:58'),(14,24,4,8,4,'Sửa chữa có tâm, máy hoạt động hoàn hảo.','2026-03-25 06:21:58'),(15,29,2,9,4,'Thợ đến đúng giờ, máy chạy êm ru sau khi sửa.','2026-03-22 14:17:04'),(16,29,2,9,5,'Sửa chữa có tâm, máy hoạt động hoàn hảo.','2026-03-22 14:17:04'),(17,30,2,7,4,'Sửa chữa có tâm, máy hoạt động hoàn hảo.','2026-03-22 14:26:50'),(18,30,2,7,4,'Sửa chữa có tâm, máy hoạt động hoàn hảo.','2026-03-22 14:26:50'),(19,31,2,7,4,'Dịch vụ rất tốt, thợ nhiệt tình, sửa nhanh gọn.','2026-03-26 09:58:21'),(20,31,2,7,5,'Chất lượng tuyệt vời, giá cả hợp lý.','2026-03-26 09:58:21'),(21,32,2,7,4,'Tôi rất hài lòng với thái độ phục vụ của Fixtech.','2026-03-25 17:07:11'),(22,32,2,7,5,'Tôi rất hài lòng với thái độ phục vụ của Fixtech.','2026-03-25 17:07:11'),(23,34,2,7,4,'Dịch vụ rất tốt, thợ nhiệt tình, sửa nhanh gọn.','2026-03-26 09:44:22'),(24,34,2,7,4,'Sửa chữa có tâm, máy hoạt động hoàn hảo.','2026-03-26 09:44:22');
/*!40000 ALTER TABLE `repair_review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repair_service`
--

DROP TABLE IF EXISTS `repair_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repair_service` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `short_description` varchar(500) DEFAULT NULL,
  `description` text,
  `base_price` bigint DEFAULT '0',
  `estimated_minutes` int DEFAULT '0',
  `warranty_days` int DEFAULT '0',
  `active` bit(1) DEFAULT b'1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_repair_service_category_id` (`category_id`),
  KEY `idx_repair_service_active` (`active`),
  CONSTRAINT `fk_repair_service_category` FOREIGN KEY (`category_id`) REFERENCES `repair_service_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repair_service`
--

LOCK TABLES `repair_service` WRITE;
/*!40000 ALTER TABLE `repair_service` DISABLE KEYS */;
INSERT INTO `repair_service` VALUES (1,1,'Vệ sinh & Bảo dưỡng','Vệ sinh & Bảo dưỡng','Quy trình 12 bước vệ sinh chuyên sâu',150000,45,30,_binary '','2025-12-23 14:55:16','2026-06-12 15:47:35'),(2,1,'Sửa chữa Phần cứng','Sửa chữa Phần cứng','Chuyên trị các bệnh khó của Laptop',300000,120,30,_binary '','2025-12-23 14:55:16','2026-06-12 15:47:35'),(3,1,'Cài đặt Phần mềm','Cài đặt Phần mềm','Cài đặt Windows, MacOS, Office',100000,30,30,_binary '','2025-12-23 14:55:16','2026-06-12 15:47:35'),(4,1,'Nâng cấp Linh kiện','Nâng cấp Linh kiện','Tăng tốc máy tính với SSD và RAM',500000,60,30,_binary '','2025-12-23 14:55:16','2026-06-12 15:47:35');
/*!40000 ALTER TABLE `repair_service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repair_service_category`
--

DROP TABLE IF EXISTS `repair_service_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repair_service_category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `active` bit(1) DEFAULT b'1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_repair_service_category_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repair_service_category`
--

LOCK TABLES `repair_service_category` WRITE;
/*!40000 ALTER TABLE `repair_service_category` DISABLE KEYS */;
INSERT INTO `repair_service_category` VALUES (1,'Dịch vụ chung','Danh mục chuyển từ hệ thống cũ',_binary '','2026-06-12 15:47:35','2026-06-12 16:09:26');
/*!40000 ALTER TABLE `repair_service_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(200) DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `plain_password` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `role` varchar(30) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `active` bit(1) DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `phone` (`phone`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Administrator','1','$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123',NULL,'ADMIN','2026-05-25 04:26:00',_binary ''),(2,'Administrator 2','0900000002','$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123','admin1@techfix.pro','ADMIN','2025-12-23 14:55:16',_binary ''),(3,'Technician 1','0900000003','$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123','tech@techfix.pro','TECHNICIAN','2025-12-23 14:55:16',_binary ''),(4,'Technician 2','0900000004','$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123','tech1@techfix.pro','TECHNICIAN','2025-12-23 14:55:16',_binary ''),(5,'Technician 3','0900000005','$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123','tech2@techfix.pro','TECHNICIAN','2025-12-23 14:55:16',_binary ''),(6,'Technician 4','0900000006','$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123','tech3@techfix.pro','TECHNICIAN','2025-12-23 14:55:16',_binary ''),(7,'tester QA','0377','$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123','test@mail.com','CUSTOMER','2025-12-23 14:55:16',_binary ''),(8,'Vu LN','0653214879','$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123','vu@mail.com','CUSTOMER','2025-12-23 15:07:46',_binary ''),(9,'Ptm Châu','0965874321','$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123','chau@mail.com','CUSTOMER','2025-12-23 15:11:27',_binary ''),(10,'Phương Anh','0645213987','$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123','phanh@mail.com','CUSTOMER','2025-12-23 15:15:07',_binary ''),(11,'Chu Tổng','096531287','$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123','tuan@mail.com','CUSTOMER','2025-12-23 15:18:37',_binary ''),(12,'Trường Le','0692531874','$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123','truong@mail.com','CUSTOMER','2025-12-23 15:20:52',_binary ''),(13,'Minh Anh','0645832179','$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123','manh@mail.com','CUSTOMER','2026-01-06 07:03:56',_binary ''),(14,'Minh Anh',NULL,'$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123','manh@gmail.com','CUSTOMER','2026-03-19 07:42:54',_binary ''),(15,'Trần Tích','2','$2a$10$Ezei/6tfBFfMGovNFVdd7.A4LWdh4E5qa9GwwtA5dkvykSTHgansa','123',NULL,NULL,'2026-06-12 16:42:26',_binary '');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory_item`
--

DROP TABLE IF EXISTS `inventory_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `sku` varchar(100) DEFAULT NULL,
  `cost_price` decimal(10,2) DEFAULT '0.00',
  `selling_price` decimal(10,2) DEFAULT '0.00',
  `stock_quantity` int DEFAULT '0',
  `image_url` varchar(255) DEFAULT NULL,
  `description` text,
  `active` bit(1) DEFAULT b'1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inventory_item_sku` (`sku`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory_item`
--

LOCK TABLES `inventory_item` WRITE;
/*!40000 ALTER TABLE `inventory_item` DISABLE KEYS */;
INSERT INTO `inventory_item` VALUES 
(1,'Màn hình iPhone 13 Pro Max','IP13PM-SCR',2000000.00,3500000.00,10,NULL,'Màn hình OLED chính hãng thay thế cho iPhone 13 Pro Max',_binary ' ','2026-06-13 10:00:00','2026-06-13 10:00:00'),
(2,'Pin iPhone 13 Pro Max','IP13PM-BAT',500000.00,800000.00,25,NULL,'Pin dung lượng chuẩn cho iPhone 13 Pro Max',_binary ' ','2026-06-13 10:00:00','2026-06-13 10:00:00'),
(3,'Mặt kính Samsung S22 Ultra','SS22U-GL',300000.00,600000.00,15,NULL,'Mặt kính cường lực thay thế cho Samsung S22 Ultra',_binary ' ','2026-06-13 10:00:00','2026-06-13 10:00:00'),
(4,'Cáp sạc Type-C 20W','CBL-TC-20W',50000.00,150000.00,100,NULL,'Cáp sạc nhanh Type-C 20W',_binary ' ','2026-06-13 10:00:00','2026-06-13 10:00:00'),
(5,'Miếng dán cường lực chống nhìn trộm','PROT-PRIV',20000.00,100000.00,50,NULL,'Kính cường lực chống nhìn trộm cho các dòng máy',_binary ' ','2026-06-13 10:00:00','2026-06-13 10:00:00');
/*!40000 ALTER TABLE `inventory_item` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-12 23:48:30
