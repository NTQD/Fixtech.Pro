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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `repair_service_category` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(200) NOT NULL,
    `description` varchar(500) DEFAULT NULL,
    `active` bit(1) DEFAULT b'1',
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_repair_service_category_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `repair_service` (
    `id` int NOT NULL AUTO_INCREMENT,
    `category_id` int NOT NULL,
    `name` varchar(255) NOT NULL,
    `short_description` varchar(500) DEFAULT NULL,
    `description` text,
    `base_price` bigint DEFAULT 0,
    `estimated_minutes` int DEFAULT 0,
    `warranty_days` int DEFAULT 0,
    `active` bit(1) DEFAULT b'1',
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_repair_service_category_id` (`category_id`),
    KEY `idx_repair_service_active` (`active`),
    CONSTRAINT `fk_repair_service_category` FOREIGN KEY (`category_id`) REFERENCES `repair_service_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
    `total_estimated_price` bigint DEFAULT 0,
    `total_estimated_minutes` int DEFAULT 0,
    `scheduled_at` datetime DEFAULT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_repair_booking_code` (`booking_code`),
    KEY `idx_repair_booking_customer_id` (`customer_id`),
    KEY `idx_repair_booking_status` (`status`),
    KEY `idx_repair_booking_technician_id` (`technician_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `repair_booking_item` (
    `id` int NOT NULL AUTO_INCREMENT,
    `booking_id` int NOT NULL,
    `service_id` int NOT NULL,
    `service_name` varchar(255) NOT NULL,
    `quantity` int DEFAULT 1,
    `estimated_price` bigint DEFAULT 0,
    `final_price` bigint DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_repair_booking_item_booking_id` (`booking_id`),
    KEY `idx_repair_booking_item_service_id` (`service_id`),
    CONSTRAINT `fk_repair_booking_item_booking` FOREIGN KEY (`booking_id`) REFERENCES `repair_booking` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_repair_booking_item_service` FOREIGN KEY (`service_id`) REFERENCES `repair_service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `repair_booking_note` (
    `id` int NOT NULL AUTO_INCREMENT,
    `booking_id` int NOT NULL,
    `note` varchar(1000) NOT NULL,
    `created_by` int DEFAULT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_repair_booking_note_booking_id` (`booking_id`),
    CONSTRAINT `fk_repair_booking_note_booking` FOREIGN KEY (`booking_id`) REFERENCES `repair_booking` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
