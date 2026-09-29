CREATE TABLE `users` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `loyalty_level_id` int,
  `name` varchar(50) NOT NULL,
  `email` varchar(100) UNIQUE NOT NULL,
  `email_verified` tinyint DEFAULT 0,
  `phone` varchar(20) UNIQUE NOT NULL,
  `password` varchar(255) NOT NULL,
  `xp_points` int NOT NULL DEFAULT 0,
  `credit_balance` decimal(10,2) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT (now()),
  `updated_at` datetime,
  `deleted_at` datetime,
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0
);

CREATE TABLE `admins` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `email` varchar(100) UNIQUE NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT (now()),
  `updated_at` datetime,
  `deleted_at` datetime,
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0
);

CREATE TABLE `password_reset_tokens` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `token` varchar(255) UNIQUE NOT NULL,
  `expires_at` datetime NOT NULL,
  `used` boolean NOT NULL DEFAULT false,
  `created_at` datetime NOT NULL DEFAULT (now())
);

CREATE TABLE `workers` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `phone` varchar(20) UNIQUE NOT NULL,
  `email` varchar(100) UNIQUE,
  `password` varchar(255) NOT NULL,
  `position` varchar(50),
  `active` boolean NOT NULL DEFAULT true,
  `created_at` datetime NOT NULL DEFAULT (now()),
  `updated_at` datetime,
  `deleted_at` datetime,
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0
);

CREATE TABLE `brands` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(50) UNIQUE NOT NULL
);

CREATE TABLE `vehicles` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `brand_id` int,
  `license_plate` varchar(20),
  `vehicle_type` enum('sedan','suv','truck','van','motorcycle','other'),
  `created_at` datetime NOT NULL DEFAULT (now()),
  `updated_at` datetime,
  `deleted_at` datetime,
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0
);

CREATE TABLE `appointments` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `vehicle_id` int NOT NULL,
  `worker_id` int,
  `appointment_date` date NOT NULL,
  `appointment_time` time NOT NULL,
  `status` enum('pending','confirmed','in_progress','completed','cancelled') NOT NULL DEFAULT 'pending',
  `notes` text,
  `total_price` decimal(10,2),
  `created_at` datetime NOT NULL DEFAULT (now()),
  `updated_at` datetime
);

CREATE TABLE `appointment_services` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `appointment_id` int NOT NULL,
  `service_id` int NOT NULL,
  `quantity` int NOT NULL DEFAULT 1,
  `price` decimal(10,2) NOT NULL
);

CREATE TABLE `services` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` text,
  `price` decimal(10,2),
  `duration_minutes` int,
  `active` boolean NOT NULL DEFAULT true,
  `created_at` datetime NOT NULL DEFAULT (now()),
  `updated_at` datetime
);

CREATE TABLE `coupons` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `code` varchar(30) UNIQUE NOT NULL,
  `description` text,
  `discount_percent` decimal(5,2),
  `discount_amount` decimal(10,2),
  `valid_from` date,
  `valid_until` date,
  `usage_limit` int,
  `active` boolean NOT NULL DEFAULT true,
  `created_at` datetime NOT NULL DEFAULT (now()),
  `updated_at` datetime
);

CREATE TABLE `payments` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `appointment_id` int NOT NULL,
  `coupon_id` int,
  `amount` decimal(10,2),
  `discount_amount` decimal(10,2) NOT NULL DEFAULT 0,
  `payment_status` enum('unpaid','paid','refunded') NOT NULL DEFAULT 'unpaid',
  `payment_date` datetime COMMENT 'mikor vette át a munkás a készpénzt',
  `created_at` datetime NOT NULL DEFAULT (now()),
  `updated_at` datetime
);

CREATE TABLE `reviews` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `appointment_id` int UNIQUE NOT NULL,
  `rating` int NOT NULL,
  `comment` text,
  `created_at` datetime NOT NULL DEFAULT (now())
);

CREATE TABLE `loyalty_levels` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `min_xp` int NOT NULL,
  `appointment_priority` boolean NOT NULL DEFAULT false,
  `coupon_bonus_percent` decimal(5,2) NOT NULL DEFAULT 0
);

CREATE TABLE `xp_transactions` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `appointment_id` int,
  `xp_earned` int NOT NULL,
  `description` text,
  `created_at` datetime NOT NULL DEFAULT (now())
);

ALTER TABLE `users` ADD FOREIGN KEY (`loyalty_level_id`) REFERENCES `loyalty_levels` (`id`);

ALTER TABLE `password_reset_tokens` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `vehicles` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `vehicles` ADD FOREIGN KEY (`brand_id`) REFERENCES `brands` (`id`);

ALTER TABLE `appointments` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `appointments` ADD FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`);

ALTER TABLE `appointments` ADD FOREIGN KEY (`worker_id`) REFERENCES `workers` (`id`);

ALTER TABLE `appointment_services` ADD FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`);

ALTER TABLE `appointment_services` ADD FOREIGN KEY (`service_id`) REFERENCES `services` (`id`);

ALTER TABLE `payments` ADD FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`);

ALTER TABLE `payments` ADD FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`);

ALTER TABLE `reviews` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `reviews` ADD FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`);

ALTER TABLE `xp_transactions` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `xp_transactions` ADD FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`);
