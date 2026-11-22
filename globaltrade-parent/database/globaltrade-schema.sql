-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.33 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.4.0.6659
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for globaltrade_logistics_corporation
CREATE DATABASE IF NOT EXISTS `globaltrade_logistics_corporation` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `globaltrade_logistics_corporation`;

-- Dumping structure for table globaltrade_logistics_corporation.alerts
CREATE TABLE IF NOT EXISTS `alerts` (
  `alert_id` int NOT NULL AUTO_INCREMENT,
  `alert_type` varchar(255) NOT NULL,
  `entity_type` varchar(50) DEFAULT NULL,
  `entity_id` int DEFAULT NULL,
  `severity` varchar(255) NOT NULL,
  `message` varchar(500) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `resolved` bit(1) NOT NULL,
  PRIMARY KEY (`alert_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.audit_logs
CREATE TABLE IF NOT EXISTS `audit_logs` (
  `log_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `action` varchar(100) NOT NULL,
  `entity_type` varchar(50) NOT NULL,
  `entity_id` int NOT NULL,
  `details` tinytext,
  `logged_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`log_id`),
  KEY `fk_audit_logs_users1_idx` (`user_id`),
  CONSTRAINT `fk_audit_logs_users1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.carriers
CREATE TABLE IF NOT EXISTS `carriers` (
  `carrier_id` int NOT NULL AUTO_INCREMENT,
  `carrier_name` varchar(100) NOT NULL,
  `service_type` varchar(50) DEFAULT NULL,
  `contact_info` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`carrier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.customers
CREATE TABLE IF NOT EXISTS `customers` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `company_name` varchar(150) DEFAULT NULL,
  `contact_phone` varchar(20) DEFAULT NULL,
  `country` varchar(60) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`),
  KEY `fk_customers_users1_idx` (`user_id`),
  CONSTRAINT `fk_customers_users1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.customer_orders
CREATE TABLE IF NOT EXISTS `customer_orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `destination` varchar(150) NOT NULL,
  `status` varchar(255) NOT NULL,
  `requested_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `fk_customer_orders_customers1_idx` (`customer_id`),
  CONSTRAINT `fk_customer_orders_customers1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.customs_documents
CREATE TABLE IF NOT EXISTS `customs_documents` (
  `doc_id` int NOT NULL AUTO_INCREMENT,
  `shipment_id` int NOT NULL,
  `document_type` varchar(80) NOT NULL,
  `status` varchar(255) NOT NULL,
  `submitted_date` datetime DEFAULT NULL,
  `approved_date` datetime DEFAULT NULL,
  `customs_agent_id` int DEFAULT NULL,
  PRIMARY KEY (`doc_id`),
  KEY `fk_customs_documents_shipments1_idx` (`shipment_id`),
  KEY `fk_customs_documents_users1_idx` (`customs_agent_id`),
  CONSTRAINT `fk_customs_documents_shipments1` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`shipment_id`),
  CONSTRAINT `fk_customs_documents_users1` FOREIGN KEY (`customs_agent_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.employees
CREATE TABLE IF NOT EXISTS `employees` (
  `employee_id` int NOT NULL AUTO_INCREMENT,
  `nic_number` varchar(20) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `job_position` varchar(50) NOT NULL,
  `department` varchar(50) DEFAULT NULL,
  `hire_date` date DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `is_registered` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `nic_number_UNIQUE` (`nic_number`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.exception_logs
CREATE TABLE IF NOT EXISTS `exception_logs` (
  `exception_id` bigint NOT NULL AUTO_INCREMENT,
  `exception_type` varchar(100) NOT NULL,
  `message` text,
  `stack_trace` longtext,
  `entity_type` varchar(50) DEFAULT NULL,
  `entity_id` int DEFAULT NULL,
  `occurred_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `resolved` bit(1) NOT NULL,
  PRIMARY KEY (`exception_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.inventory_items
CREATE TABLE IF NOT EXISTS `inventory_items` (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `sku` varchar(50) NOT NULL,
  `name` varchar(150) NOT NULL,
  `warehouse_id` int NOT NULL,
  `quantity` int NOT NULL DEFAULT '0',
  `reorder_level` int NOT NULL DEFAULT '10',
  `unit_price` decimal(10,2) NOT NULL,
  `last_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`item_id`),
  UNIQUE KEY `sku_UNIQUE` (`sku`),
  KEY `fk_inventory_items_warehouses1_idx` (`warehouse_id`),
  CONSTRAINT `fk_inventory_items_warehouses1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.order_items
CREATE TABLE IF NOT EXISTS `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `item_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_order_items_customer_orders1_idx` (`order_id`),
  KEY `fk_order_items_inventory_items1_idx` (`item_id`),
  CONSTRAINT `fk_order_items_customer_orders1` FOREIGN KEY (`order_id`) REFERENCES `customer_orders` (`order_id`),
  CONSTRAINT `fk_order_items_inventory_items1` FOREIGN KEY (`item_id`) REFERENCES `inventory_items` (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.roles
CREATE TABLE IF NOT EXISTS `roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name_UNIQUE` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.route_plans
CREATE TABLE IF NOT EXISTS `route_plans` (
  `route_id` int NOT NULL AUTO_INCREMENT,
  `shipment_id` int NOT NULL,
  `planned_route` tinytext NOT NULL,
  `optimized_route` tinytext,
  `distance_km` decimal(8,2) DEFAULT NULL,
  `estimated_time_hours` decimal(6,2) DEFAULT NULL,
  `calculated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`route_id`),
  KEY `fk_route_plans_shipments1_idx` (`shipment_id`),
  CONSTRAINT `fk_route_plans_shipments1` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`shipment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.shipments
CREATE TABLE IF NOT EXISTS `shipments` (
  `shipment_id` int NOT NULL AUTO_INCREMENT,
  `tracking_number` varchar(50) NOT NULL,
  `origin_warehouse_id` int NOT NULL,
  `destination` varchar(150) NOT NULL,
  `vendor_id` int NOT NULL,
  `carrier_id` int NOT NULL,
  `status` varchar(255) NOT NULL,
  `scheduled_date` datetime NOT NULL,
  `estimated_delivery_date` datetime DEFAULT NULL,
  `actual_delivery_date` datetime DEFAULT NULL,
  `created_by` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`shipment_id`),
  UNIQUE KEY `tracking_number_UNIQUE` (`tracking_number`),
  KEY `fk_shipments_warehouses1_idx` (`origin_warehouse_id`),
  KEY `fk_shipments_vendors1_idx` (`vendor_id`),
  KEY `fk_shipments_carriers1_idx` (`carrier_id`),
  KEY `fk_shipments_users1_idx` (`created_by`),
  CONSTRAINT `fk_shipments_carriers1` FOREIGN KEY (`carrier_id`) REFERENCES `carriers` (`carrier_id`),
  CONSTRAINT `fk_shipments_users1` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_shipments_vendors1` FOREIGN KEY (`vendor_id`) REFERENCES `vendors` (`vendor_id`),
  CONSTRAINT `fk_shipments_warehouses1` FOREIGN KEY (`origin_warehouse_id`) REFERENCES `warehouses` (`warehouse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.shipment_items
CREATE TABLE IF NOT EXISTS `shipment_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shipment_id` int NOT NULL,
  `item_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_shipments_has_inventory_items_inventory_items1_idx` (`item_id`),
  KEY `fk_shipments_has_inventory_items_shipments1_idx` (`shipment_id`),
  CONSTRAINT `fk_shipments_has_inventory_items_inventory_items1` FOREIGN KEY (`item_id`) REFERENCES `inventory_items` (`item_id`),
  CONSTRAINT `fk_shipments_has_inventory_items_shipments1` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`shipment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.shipment_orders
CREATE TABLE IF NOT EXISTS `shipment_orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `shipment_id` int NOT NULL,
  `order_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_shipment_orders_shipments1_idx` (`shipment_id`),
  KEY `fk_shipment_orders_customer_orders1_idx` (`order_id`),
  CONSTRAINT `fk_shipment_orders_customer_orders1` FOREIGN KEY (`order_id`) REFERENCES `customer_orders` (`order_id`),
  CONSTRAINT `fk_shipment_orders_shipments1` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`shipment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.shipment_status_history
CREATE TABLE IF NOT EXISTS `shipment_status_history` (
  `history_id` bigint NOT NULL AUTO_INCREMENT,
  `shipment_id` int NOT NULL,
  `old_status` varchar(255) DEFAULT NULL,
  `new_status` varchar(255) NOT NULL,
  `changed_by` int NOT NULL,
  `changed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`history_id`),
  KEY `fk_shipment_status_history_shipments1_idx` (`shipment_id`),
  KEY `fk_shipment_status_history_users1_idx` (`changed_by`),
  CONSTRAINT `fk_shipment_status_history_shipments1` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`shipment_id`),
  CONSTRAINT `fk_shipment_status_history_users1` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.system_timers
CREATE TABLE IF NOT EXISTS `system_timers` (
  `timer_id` int NOT NULL AUTO_INCREMENT,
  `timer_name` varchar(100) NOT NULL,
  `timer_type` varchar(255) NOT NULL,
  `schedule_expression` varchar(100) DEFAULT NULL,
  `last_run` timestamp NULL DEFAULT NULL,
  `next_run` timestamp NULL DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  PRIMARY KEY (`timer_id`),
  UNIQUE KEY `timer_name_UNIQUE` (`timer_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.users
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `role_id` int NOT NULL,
  `vendor_id` int DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  KEY `fk_users_roles_idx` (`role_id`),
  KEY `fk_users_vendors1_idx` (`vendor_id`),
  CONSTRAINT `fk_users_roles` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`),
  CONSTRAINT `fk_users_vendors1` FOREIGN KEY (`vendor_id`) REFERENCES `vendors` (`vendor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.vendors
CREATE TABLE IF NOT EXISTS `vendors` (
  `vendor_id` int NOT NULL AUTO_INCREMENT,
  `vendor_name` varchar(150) NOT NULL,
  `contact_email` varchar(100) NOT NULL,
  `contact_phone` varchar(20) DEFAULT NULL,
  `country` varchar(60) NOT NULL,
  `performance_rating` decimal(3,2) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`vendor_id`),
  UNIQUE KEY `contact_email_UNIQUE` (`contact_email`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.vendor_performance_evaluations
CREATE TABLE IF NOT EXISTS `vendor_performance_evaluations` (
  `eval_id` int NOT NULL AUTO_INCREMENT,
  `vendor_id` int NOT NULL,
  `evaluator_id` int NOT NULL,
  `score` decimal(3,2) NOT NULL,
  `evaluation_date` date NOT NULL,
  `comments` tinytext,
  PRIMARY KEY (`eval_id`),
  KEY `fk_vendor_performance_evaluations_vendors1_idx` (`vendor_id`),
  KEY `fk_vendor_performance_evaluations_users1_idx` (`evaluator_id`),
  CONSTRAINT `fk_vendor_performance_evaluations_users1` FOREIGN KEY (`evaluator_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_vendor_performance_evaluations_vendors1` FOREIGN KEY (`vendor_id`) REFERENCES `vendors` (`vendor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table globaltrade_logistics_corporation.warehouses
CREATE TABLE IF NOT EXISTS `warehouses` (
  `warehouse_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `location` varchar(150) NOT NULL,
  `country` varchar(60) NOT NULL,
  `capacity` int NOT NULL,
  PRIMARY KEY (`warehouse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
