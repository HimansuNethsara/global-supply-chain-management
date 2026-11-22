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

-- Dumping data for table globaltrade_logistics_corporation.alerts: ~5 rows (approximately)
INSERT INTO `alerts` (`alert_id`, `alert_type`, `entity_type`, `entity_id`, `severity`, `message`, `created_at`, `resolved`) VALUES
	(1, 'LOW_INVENTORY', 'InventoryItem', 21, 'HIGH', 'Low stock alert: Item \'Paper Products - Office Supplies\' (SKU: SKU-US-003) has 850 units remaining. Reorder level is 900.', '2026-09-01 12:15:00', b'0'),
	(2, 'LOW_INVENTORY', 'InventoryItem', 24, 'HIGH', 'Low stock alert: Item \'Robotics - Servo Motors\' (SKU: SKU-JP-003) has 150 units remaining. Reorder level is 200.', '2026-09-01 12:15:00', b'0'),
	(3, 'LOW_INVENTORY', 'InventoryItem', 25, 'HIGH', 'Low stock alert: Item \'Pharmaceuticals - Pain Relief Medication\' (SKU: SKU-UK-001) has 700 units remaining. Reorder level is 800.', '2026-09-01 12:15:00', b'0'),
	(4, 'CUSTOMS_DEADLINE', 'CustomsDocument', 4, 'HIGH', 'Customs document 4 (Type: Export License) for shipment LAM-GFE-AIR-260901192412 has been submitted for approval more than 12 hours ago.', '2026-09-02 05:00:00', b'0'),
	(5, 'CUSTOMS_DEADLINE', 'CustomsDocument', 3, 'MEDIUM', 'Customs document 3 (Type: Certificate of Origin) for shipment SOD-TLA-SEA-260901191840 is pending for more than 24 hours.', '2026-09-02 13:50:00', b'0');

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

-- Dumping data for table globaltrade_logistics_corporation.audit_logs: ~25 rows (approximately)
INSERT INTO `audit_logs` (`log_id`, `user_id`, `action`, `entity_type`, `entity_id`, `details`, `logged_at`) VALUES
	(1, 1, 'UPDATE', 'VENDOR', 10, 'VendorService.updateVendor | User: maverick | Success', '2026-09-01 11:37:38'),
	(2, 1, 'UPDATE', 'VENDOR', 10, 'VendorService.updateVendor | User: maverick | Success', '2026-09-01 11:37:42'),
	(3, 1, 'CREATE', 'VENDOR', 0, 'VendorService.createVendor | User: maverick | Success', '2026-09-01 11:40:40'),
	(4, 1, 'UPDATE', 'VENDOR', 11, 'VendorService.updateVendor | User: maverick | Success', '2026-09-01 11:44:55'),
	(5, 1, 'CREATE', 'WAREHOUSE', 50000, 'WarehouseService.createWarehouse | User: maverick | Success', '2026-09-01 11:47:20'),
	(6, 1, 'CREATE', 'CARRIER', 0, 'CarrierService.createCarrier | User: maverick | Success', '2026-09-01 11:57:53'),
	(7, 1, 'CREATE', 'INVENTORY', 10, 'InventoryService.createItem | User: maverick | Success', '2026-09-01 12:11:58'),
	(8, 5, 'CREATE', 'CUSTOMER', 1, 'CustomerOrderService.createOrder | User: littleangel | Success', '2026-09-01 12:50:16'),
	(9, 5, 'CREATE', 'CUSTOMER', 1, 'CustomerOrderService.createOrder | User: littleangel | Success', '2026-09-01 12:58:40'),
	(10, 5, 'CREATE', 'CUSTOMER', 1, 'CustomerOrderService.createOrder | User: littleangel | Success', '2026-09-01 13:02:10'),
	(11, 5, 'CREATE', 'CUSTOMER', 1, 'CustomerOrderService.createOrder | User: littleangel | Success', '2026-09-01 13:05:00'),
	(12, 5, 'CREATE', 'CUSTOMER', 1, 'CustomerOrderService.createOrder | User: littleangel | Success', '2026-09-01 13:08:01'),
	(13, 5, 'CREATE', 'CUSTOMER', 1, 'CustomerOrderService.createOrder | User: littleangel | Success', '2026-09-01 13:10:23'),
	(14, 5, 'CREATE', 'CUSTOMER', 1, 'CustomerOrderService.createOrder | User: littleangel | Success', '2026-09-01 13:12:07'),
	(15, 5, 'DELETE', 'CUSTOMER', 7, 'CustomerOrderService.cancelOrder | User: littleangel | Success', '2026-09-01 13:12:18'),
	(16, 1, 'CREATE', 'CUSTOMER', 1, 'CustomerOrderService.createOrder | User: maverick | Success', '2026-09-01 13:14:28'),
	(17, 1, 'CREATE', 'CUSTOMER', 1, 'CustomerOrderService.createOrder | User: maverick | Success', '2026-09-01 13:16:36'),
	(18, 1, 'CREATE', 'SHIPMENT', 1, 'ShipmentService.createShipment | User: maverick | Success', '2026-09-01 13:27:44'),
	(19, 1, 'UPDATE', 'SHIPMENT', 1, 'ShipmentService.updateShipmentStatus | User: maverick | Success', '2026-09-01 13:27:52'),
	(20, 1, 'CREATE', 'SHIPMENT', 2, 'ShipmentService.createShipment | User: maverick | Success', '2026-09-01 13:43:14'),
	(21, 1, 'UPDATE', 'SHIPMENT', 2, 'ShipmentService.updateShipmentStatus | User: maverick | Success', '2026-09-01 13:44:03'),
	(22, 5, 'CREATE', 'SHIPMENT', 6, 'ShipmentService.createShipment | User: littleangel | Success', '2026-09-01 13:48:41'),
	(23, 5, 'CREATE', 'SHIPMENT', 1, 'ShipmentService.createShipment | User: littleangel | Success', '2026-09-01 13:54:13'),
	(24, 1, 'UPDATE', 'SHIPMENT', 1, 'ShipmentService.updateShipmentStatus | User: maverick | Success', '2026-09-01 13:58:35'),
	(25, 1, 'UPDATE', 'SHIPMENT', 2, 'ShipmentService.updateShipmentStatus | User: maverick | Success', '2026-09-01 13:58:42');

-- Dumping structure for table globaltrade_logistics_corporation.carriers
CREATE TABLE IF NOT EXISTS `carriers` (
  `carrier_id` int NOT NULL AUTO_INCREMENT,
  `carrier_name` varchar(100) NOT NULL,
  `service_type` varchar(50) DEFAULT NULL,
  `contact_info` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`carrier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table globaltrade_logistics_corporation.carriers: ~14 rows (approximately)
INSERT INTO `carriers` (`carrier_id`, `carrier_name`, `service_type`, `contact_info`) VALUES
	(1, 'FedEx Global', 'Air Freight', '1-800-463-3339 | fedex.com/contact'),
	(2, 'DHL International', 'Air', '+1-800-225-5345 | dhl.com/support'),
	(3, 'Americold Logistics', 'Cold Chain Logistics', '1-888-808-4877 | americold.com/contact'),
	(4, 'Lineage Logistics', 'Cold Chain', '+1-855-546-3243 | lineage.com/contact'),
	(5, 'Expeditors International', 'Customs Brokerage', '+1-206-674-3400 | expeditors.com/contact'),
	(6, 'UPS Express', 'Express', '1-800-742-5877 | ups.com/contact'),
	(7, 'Kuehne + Nagel', 'Freight Forwarding', '+1-201-413-5500 | kuehne-nagel.com/contact'),
	(8, 'XPO Logistics', 'Ground Shipping', '1-844-976-6955 | xpo.com/contact'),
	(9, 'Estes Express Lines', 'Ground', '1-866-334-3333 | estes-express.com/contact'),
	(10, 'Hub Group', 'Intermodal Transport', '1-630-271-3600 | hubgroup.com/contact'),
	(11, 'J.B. Hunt Transport', 'Intermodal', '1-800-643-3622 | jbhunt.com/contact'),
	(12, 'Maersk Line', 'Sea Freight', '+45-3363-3363 | maersk.com/contact'),
	(13, 'MSC Mediterranean Shipping', 'Sea', '+41-22-703-8888 | msccargo.com/contact'),
	(14, 'Nippon Express', 'Standard', '+81-3-6253-1111 | nipponexpress.com/contact');

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

-- Dumping data for table globaltrade_logistics_corporation.customers: ~2 rows (approximately)
INSERT INTO `customers` (`customer_id`, `user_id`, `company_name`, `contact_phone`, `country`, `created_at`) VALUES
	(1, 5, NULL, NULL, 'Sri Lanka', '2026-09-01 07:36:28'),
	(2, 7, NULL, NULL, 'Sri Lanka', '2026-09-01 07:38:39'),
	(3, 9, NULL, NULL, 'Sri Lanka', '2026-09-01 07:42:15');

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

-- Dumping data for table globaltrade_logistics_corporation.customer_orders: ~8 rows (approximately)
INSERT INTO `customer_orders` (`order_id`, `customer_id`, `destination`, `status`, `requested_date`, `notes`) VALUES
	(1, 1, '45 Gangnam-daero, Gangnam-gu, Seoul, South Korea', 'PENDING', '2026-09-01 12:50:16', 'Express delivery requested for cosmetics products'),
	(2, 2, '221B Baker Street, London NW1 6XE, UK', 'ASSIGNED', '2026-09-01 12:58:40', 'Fragile items - handle with care'),
	(3, 3, '1 Red Square, Moscow, Russia 109012', 'PENDING', '2026-09-01 13:02:10', ''),
	(4, 1, '15 Myeongdong-gil, Jung-gu, Seoul, South Korea', 'ASSIGNED', '2026-09-01 13:05:00', 'Urgent - required by xxx'),
	(5, 2, '10 Downing Street, London SW1A 2AA, UK', 'FULFILLED', '2026-09-01 13:08:01', 'VIP customer - priority handling'),
	(6, 3, '7 Ecke SchÃ¶nhauser Allee, Berlin 10119, Germany', 'PENDING', '2026-09-01 13:10:23', 'Requires climate-controlled shipment'),
	(7, 2, '4 Privet Drive, Little Whinging, Surrey, UK', 'CANCELLED', '2026-09-01 13:12:07', '\nCancelled: Cancelled by customer'),
	(8, 3, '15 Avenue des Champs-ÃlysÃ©es, Paris 75008, France', 'FULFILLED', '2026-09-01 13:14:28', ''),
	(9, 1, '28-3 Myeongdong 8-gil, Jung-gu, Seoul, South Korea', 'ASSIGNED', '2026-09-01 13:16:36', 'Large order - multiple items');

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

-- Dumping data for table globaltrade_logistics_corporation.customs_documents: ~5 rows (approximately)
INSERT INTO `customs_documents` (`doc_id`, `shipment_id`, `document_type`, `status`, `submitted_date`, `approved_date`, `customs_agent_id`) VALUES
	(1, 1, 'Export Declaration', 'APPROVED', '2026-09-01 19:24:45', '2026-09-01 19:28:05', 11),
	(2, 2, 'Bill of Lading', 'APPROVED', '2026-09-01 19:25:00', '2026-09-01 19:27:58', 11),
	(3, 3, 'Certificate of Origin', 'PENDING', NULL, NULL, NULL),
	(4, 4, 'Export License', 'SUBMITTED', '2026-09-01 19:25:49', NULL, NULL),
	(5, 3, 'Air Waybill', 'REJECTED', '2026-09-01 19:26:42', '2026-09-01 19:27:40', 11);

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

-- Dumping data for table globaltrade_logistics_corporation.employees: ~8 rows (approximately)
INSERT INTO `employees` (`employee_id`, `nic_number`, `full_name`, `job_position`, `department`, `hire_date`, `status`, `is_registered`) VALUES
	(1, '971234567V', 'Tom Cruise', 'LOGISTICS_COORDINATOR', 'Logistics Operations', '2018-06-15', 'ACTIVE', 1),
	(2, '981245678V', 'Florence Pugh', 'LOGISTICS_COORDINATOR', 'Logistics Operations', '2019-03-20', 'ACTIVE', 1),
	(3, '961256789V', 'Scarlett Johansson', 'WAREHOUSE_MANAGER', 'Warehouse Management', '2017-11-01', 'ACTIVE', 1),
	(4, '200112345678', 'Zendaya Maree', 'CUSTOMS_AGENT', 'Customs Compliance', '2022-02-14', 'ACTIVE', 0),
	(5, '981267890V', 'Margot Robbie', 'ACCOUNTANT', 'Finance Department', '2019-09-10', 'ACTIVE', 0),
	(6, '200234567890', 'Timothee Chalamet', 'CUSTOMER_SERVICE', 'Customer Relations', '2021-07-05', 'ACTIVE', 0),
	(7, '991278901V', 'Henry Cavill', 'IT_SUPPORT', 'Information Technology', '2020-01-20', 'ACTIVE', 0),
	(8, '200345678901', 'Emma Stone', 'HR_MANAGER', 'Human Resources', '2020-05-15', 'ACTIVE', 0);

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

-- Dumping data for table globaltrade_logistics_corporation.exception_logs: ~3 rows (approximately)
INSERT INTO `exception_logs` (`exception_id`, `exception_type`, `message`, `stack_trace`, `entity_type`, `entity_id`, `occurred_at`, `resolved`) VALUES
	(1, 'EJBTransactionRolledbackException', 'SystemException: No message', 'jakarta.ejb.EJBTransactionRolledbackException\r\n	at com.sun.ejb.containers.BaseContainer.mapLocal3xException(BaseContainer.java:2048)\r\n	at com.sun.ejb.containers.BaseContainer.postInvoke(BaseContainer.java:1858)\r\n	at com.sun.ejb.containers.BaseContainer.postInvoke(BaseContainer.java:1780)\r\n	at com.sun.ejb.containers.EJBLocalObjectInvocationHandler.invoke(EJBLocalObjectInvocationHandler.java:176)\r\n	at com.sun.ejb.containers.EJBLocalObjectInvocationHandlerDelegate.invoke(EJBLocalObjectInvocationHandlerDelegate.java:61)\r\n	at jdk.proxy116/jdk.proxy116.$Proxy2160.create(Unknown Source)\r\n	at lk.jiat.global.dao.__EJB31_Generated__VendorPerformanceEvaluationDAO__Intf____Bean__.create(Unknown Source)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.jboss.weld.util.reflection.Reflections.invokeAndUnwrap(Reflections.java:412)\r\n	at org.jboss.weld.module.ejb.EnterpriseBeanProxyMethodHandler.invoke(EnterpriseBeanProxyMethodHandler.java:133)\r\n	at org.jboss.weld.bean.proxy.EnterpriseTargetBeanInstance.invoke(EnterpriseTargetBeanInstance.java:54)\r\n	at org.jboss.weld.module.ejb.InjectionPointPropagatingEnterpriseTargetBeanInstance.invoke(InjectionPointPropagatingEnterpriseTargetBeanInstance.java:69)\r\n	at org.jboss.weld.bean.proxy.ProxyMethodHandler.invoke(ProxyMethodHandler.java:109)\r\n	at lk.jiat.global.dao.VendorPerformanceEvaluationDAO$Proxy$_$$_Weld$EnterpriseProxy$.create(Unknown Source)\r\n	at lk.jiat.global.service.VendorPerformanceEvaluationService.createEvaluation(VendorPerformanceEvaluationService.java:76)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.glassfish.exousia.AuthorizationService.lambda$invokeBeanMethod$1(AuthorizationService.java:506)\r\n	at org.glassfish.exousia.AuthorizationService.runInScope(AuthorizationService.java:586)\r\n	at org.glassfish.exousia.AuthorizationService.invokeBeanMethod(AuthorizationService.java:506)\r\n	at org.glassfish.ejb.security.application.EJBSecurityManager.invoke(EJBSecurityManager.java:297)\r\n	at com.sun.ejb.containers.BaseContainer.invokeBeanMethod(BaseContainer.java:4193)\r\n	at com.sun.ejb.EjbInvocation.invokeBeanMethod(EjbInvocation.java:604)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeChainImpl.invokeNext(InterceptorManager.java:706)\r\n	at com.sun.ejb.EjbInvocation.proceed(EjbInvocation.java:553)\r\n	at lk.jiat.global.interceptor.ExceptionLoggingInterceptor.logExceptions(ExceptionLoggingInterceptor.java:20)\r\n	at jdk.internal.reflect.GeneratedMethodAccessor2104.invoke(Unknown Source)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeInterceptor.intercept(InterceptorManager.java:766)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeChainImpl.invokeNext(InterceptorManager.java:706)\r\n	at com.sun.ejb.EjbInvocation.proceed(EjbInvocation.java:553)\r\n	at org.jboss.weld.module.ejb.AbstractEJBRequestScopeActivationInterceptor.aroundInvoke(AbstractEJBRequestScopeActivationInterceptor.java:78)\r\n	at org.jboss.weld.module.ejb.SessionBeanInterceptor.aroundInvoke(SessionBeanInterceptor.java:52)\r\n	at jdk.internal.reflect.GeneratedMethodAccessor2093.invoke(Unknown Source)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.re\n... (truncated)', 'VendorPerformanceEvaluationService', 11, '2026-09-01 11:42:25', b'0'),
	(2, 'InvalidCredentialsException', 'ApplicationException: Invalid username or password', 'lk.jiat.global.exception.InvalidCredentialsException: Invalid username or password\r\n	at lk.jiat.global.service.UserService.authenticate(UserService.java:186)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.glassfish.exousia.AuthorizationService.lambda$invokeBeanMethod$1(AuthorizationService.java:506)\r\n	at org.glassfish.exousia.AuthorizationService.runInScope(AuthorizationService.java:586)\r\n	at org.glassfish.exousia.AuthorizationService.invokeBeanMethod(AuthorizationService.java:506)\r\n	at org.glassfish.ejb.security.application.EJBSecurityManager.invoke(EJBSecurityManager.java:297)\r\n	at com.sun.ejb.containers.BaseContainer.invokeBeanMethod(BaseContainer.java:4193)\r\n	at com.sun.ejb.EjbInvocation.invokeBeanMethod(EjbInvocation.java:604)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeChainImpl.invokeNext(InterceptorManager.java:706)\r\n	at com.sun.ejb.EjbInvocation.proceed(EjbInvocation.java:553)\r\n	at lk.jiat.global.interceptor.ExceptionLoggingInterceptor.logExceptions(ExceptionLoggingInterceptor.java:20)\r\n	at jdk.internal.reflect.GeneratedMethodAccessor2274.invoke(Unknown Source)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeInterceptor.intercept(InterceptorManager.java:766)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeChainImpl.invokeNext(InterceptorManager.java:706)\r\n	at com.sun.ejb.EjbInvocation.proceed(EjbInvocation.java:553)\r\n	at org.jboss.weld.module.ejb.AbstractEJBRequestScopeActivationInterceptor.aroundInvoke(AbstractEJBRequestScopeActivationInterceptor.java:78)\r\n	at org.jboss.weld.module.ejb.SessionBeanInterceptor.aroundInvoke(SessionBeanInterceptor.java:52)\r\n	at jdk.internal.reflect.GeneratedMethodAccessor2258.invoke(Unknown Source)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeInterceptor.intercept(InterceptorManager.java:766)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeChainImpl.invokeNext(InterceptorManager.java:706)\r\n	at com.sun.ejb.EjbInvocation.proceed(EjbInvocation.java:553)\r\n	at com.sun.ejb.containers.interceptors.SystemInterceptorProxy.doCall(SystemInterceptorProxy.java:122)\r\n	at com.sun.ejb.containers.interceptors.SystemInterceptorProxy.aroundInvoke(SystemInterceptorProxy.java:99)\r\n	at jdk.internal.reflect.GeneratedMethodAccessor2259.invoke(Unknown Source)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeInterceptor.intercept(InterceptorManager.java:766)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeChainImpl.invokeNext(InterceptorManager.java:706)\r\n	at com.sun.ejb.containers.interceptors.InterceptorManager.intercept(InterceptorManager.java:307)\r\n	at com.sun.ejb.containers.BaseContainer.__intercept(BaseContainer.java:4168)\r\n	at com.sun.ejb.containers.BaseContainer.intercept(BaseContainer.java:4158)\r\n	at com.sun.ejb.containers.EJBLocalObjectInvocationHandler.invoke(EJBLocalObjectInvocationHandler.java:169)\r\n	at com.sun.ejb.containers.EJBLocalObjectInvocationHandlerDelegate.invoke(EJBLocalObjectInvocationHandlerDelegate.java:61)\r\n	at jdk.proxy119/jdk.proxy119.$Proxy2254.authenticate(Unknown Source)\r\n	at lk.jiat.global.web.LoginServlet.doPost(LoginServlet.java:54)\r\n	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:547)\r\n	at jakart\n... (truncated)', 'UserService', NULL, '2026-09-01 12:47:43', b'0'),
	(3, 'BusinessException', 'ApplicationException: Cannot deliver shipment. Customs documents must be approved first.', 'lk.jiat.global.exception.BusinessException: Cannot deliver shipment. Customs documents must be approved first.\r\n	at lk.jiat.global.service.ShipmentService.updateShipmentStatus(ShipmentService.java:241)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at org.glassfish.exousia.AuthorizationService.lambda$invokeBeanMethod$1(AuthorizationService.java:506)\r\n	at org.glassfish.exousia.AuthorizationService.runInScope(AuthorizationService.java:586)\r\n	at org.glassfish.exousia.AuthorizationService.invokeBeanMethod(AuthorizationService.java:506)\r\n	at org.glassfish.ejb.security.application.EJBSecurityManager.invoke(EJBSecurityManager.java:297)\r\n	at com.sun.ejb.containers.BaseContainer.invokeBeanMethod(BaseContainer.java:4193)\r\n	at com.sun.ejb.EjbInvocation.invokeBeanMethod(EjbInvocation.java:604)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeChainImpl.invokeNext(InterceptorManager.java:706)\r\n	at com.sun.ejb.EjbInvocation.proceed(EjbInvocation.java:553)\r\n	at lk.jiat.global.interceptor.ExceptionLoggingInterceptor.logExceptions(ExceptionLoggingInterceptor.java:20)\r\n	at jdk.internal.reflect.GeneratedMethodAccessor2274.invoke(Unknown Source)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeInterceptor.intercept(InterceptorManager.java:766)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeChainImpl.invokeNext(InterceptorManager.java:706)\r\n	at com.sun.ejb.EjbInvocation.proceed(EjbInvocation.java:553)\r\n	at lk.jiat.global.interceptor.PerformanceInterceptor.measurePerformance(PerformanceInterceptor.java:42)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeInterceptor.intercept(InterceptorManager.java:766)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeChainImpl.invokeNext(InterceptorManager.java:706)\r\n	at com.sun.ejb.EjbInvocation.proceed(EjbInvocation.java:553)\r\n	at lk.jiat.global.interceptor.AuditInterceptor.audit(AuditInterceptor.java:43)\r\n	at jdk.internal.reflect.GeneratedMethodAccessor2340.invoke(Unknown Source)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeInterceptor.intercept(InterceptorManager.java:766)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeChainImpl.invokeNext(InterceptorManager.java:706)\r\n	at com.sun.ejb.EjbInvocation.proceed(EjbInvocation.java:553)\r\n	at lk.jiat.global.interceptor.ShipmentStatusUpdateInterceptor.interceptStatusUpdate(ShipmentStatusUpdateInterceptor.java:28)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\r\n	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n	at java.base/java.lang.reflect.Method.invoke(Method.java:569)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeInterceptor.intercept(InterceptorManager.java:766)\r\n	at com.sun.ejb.containers.interceptors.AroundInvokeChainImpl.invokeNext(InterceptorManager.java:706)\r\n	at com.sun.ejb.EjbInvocation.proceed(EjbInvocation.java:553)\n... (truncated)', 'ShipmentService', 4, '2026-09-01 13:58:48', b'0');

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

-- Dumping data for table globaltrade_logistics_corporation.inventory_items: ~26 rows (approximately)
INSERT INTO `inventory_items` (`item_id`, `sku`, `name`, `warehouse_id`, `quantity`, `reorder_level`, `unit_price`, `last_updated`) VALUES
	(1, 'SKU-EU-001', 'Industrial Machinery Parts - Set A', 1, 2350, 500, 45000.00, '2026-09-01 12:09:27'),
	(2, 'SKU-EU-002', 'Automotive Components - Brake Pads', 1, 1710, 300, 12500.00, '2026-09-01 12:09:27'),
	(3, 'SKU-EU-003', 'Pharmaceuticals - Antibiotics (Batch 2026)', 1, 805, 200, 32000.00, '2026-09-01 12:09:27'),
	(4, 'SKU-AS-001', 'Electronics - Smartphone Screens', 2, 3200, 600, 28000.00, '2026-09-01 12:09:27'),
	(5, 'SKU-AS-002', 'Semiconductor Chips - 5nm', 2, 1465, 400, 75000.00, '2026-09-01 12:09:27'),
	(6, 'SKU-AS-003', 'Medical Supplies - Surgical Masks', 2, 800, 300, 8500.00, '2026-09-01 12:09:27'),
	(7, 'SKU-CN-001', 'Consumer Electronics - Bluetooth Speakers', 3, 4465, 800, 6500.00, '2026-09-01 12:09:27'),
	(8, 'SKU-CN-002', 'Textiles - Silk Fabric Rolls', 3, 2800, 500, 18000.00, '2026-09-01 12:09:27'),
	(9, 'SKU-CN-003', 'Toy Manufacturing - Plastic Components', 3, 780, 200, 4200.00, '2026-09-01 12:09:27'),
	(10, 'SKU-ME-001', 'Petroleum Products - Lubricants', 4, 3800, 700, 55000.00, '2026-09-01 12:09:27'),
	(11, 'SKU-ME-002', 'Construction Materials - Steel Beams', 4, 2000, 400, 68000.00, '2026-09-01 12:09:27'),
	(12, 'SKU-ME-003', 'Food & Beverage - Packaged Dates', 4, 610, 150, 12000.00, '2026-09-01 12:09:27'),
	(13, 'SKU-OC-001', 'Agricultural Products - Wheat Grain', 5, 5755, 1000, 9500.00, '2026-09-01 12:09:27'),
	(14, 'SKU-OC-002', 'Mining Equipment - Drill Bits', 5, 1580, 300, 125000.00, '2026-09-01 12:09:27'),
	(15, 'SKU-OC-003', 'Wine & Spirits - Premium Red Wine', 5, 2480, 500, 22000.00, '2026-09-01 12:09:27'),
	(16, 'SKU-DE-001', 'Automotive - Engine Components', 6, 3150, 600, 85000.00, '2026-09-01 12:09:27'),
	(17, 'SKU-DE-002', 'Chemicals - Industrial Solvents', 6, 1850, 350, 42000.00, '2026-09-01 12:09:27'),
	(18, 'SKU-DE-003', 'Precision Tools - Measuring Instruments', 6, 530, 150, 95000.00, '2026-09-01 12:09:27'),
	(19, 'SKU-US-001', 'IT Hardware - Server Racks', 7, 2675, 500, 150000.00, '2026-09-01 12:09:27'),
	(20, 'SKU-US-002', 'Cosmetics - Luxury Skincare Sets', 7, 3390, 700, 35000.00, '2026-09-01 12:09:27'),
	(21, 'SKU-US-003', 'Paper Products - Office Supplies', 7, 850, 900, 5500.00, '2026-09-01 12:10:52'),
	(22, 'SKU-JP-001', 'Electronics - Camera Lenses', 8, 2170, 400, 120000.00, '2026-09-01 12:09:27'),
	(23, 'SKU-JP-002', 'Automotive - Electric Vehicle Batteries', 8, 1300, 300, 280000.00, '2026-09-01 12:09:27'),
	(24, 'SKU-JP-003', 'Robotics - Servo Motors', 8, 150, 200, 185000.00, '2026-09-01 12:10:49'),
	(25, 'SKU-UK-001', 'Pharmaceuticals - Pain Relief Medication', 9, 700, 800, 15000.00, '2026-09-01 12:10:39'),
	(26, 'SKU-UK-002', 'Aerospace - Aircraft Components', 9, 1100, 250, 450000.00, '2026-09-01 12:09:27'),
	(27, 'SKU-UK-003', 'Fashion - Luxury Handbags', 10, 2000, 500, 85000.00, '2026-09-01 12:12:27');

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

-- Dumping data for table globaltrade_logistics_corporation.order_items: ~19 rows (approximately)
INSERT INTO `order_items` (`id`, `order_id`, `item_id`, `quantity`) VALUES
	(1, 1, 21, 50),
	(2, 1, 22, 30),
	(3, 2, 18, 15),
	(4, 2, 17, 20),
	(5, 3, 11, 100),
	(6, 3, 12, 40),
	(7, 4, 3, 25),
	(8, 4, 2, 10),
	(9, 5, 6, 60),
	(10, 5, 5, 35),
	(11, 6, 13, 45),
	(12, 6, 14, 20),
	(13, 7, 8, 15),
	(14, 7, 9, 30),
	(15, 8, 1, 75),
	(16, 8, 2, 40),
	(17, 9, 16, 25),
	(18, 9, 17, 15),
	(19, 9, 18, 20);

-- Dumping structure for table globaltrade_logistics_corporation.roles
CREATE TABLE IF NOT EXISTS `roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name_UNIQUE` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table globaltrade_logistics_corporation.roles: ~5 rows (approximately)
INSERT INTO `roles` (`role_id`, `role_name`, `description`) VALUES
	(1, 'LOGISTICS_COORDINATOR', 'Coordinates and oversees logistics operations, shipment tracking, and supply chain coordination'),
	(2, 'CUSTOMS_AGENT', 'Manages customs documentation, compliance verification, and international trade regulations'),
	(3, 'WAREHOUSE_MANAGER', 'Oversees warehouse operations, inventory management, and storage facility coordination'),
	(4, 'VENDOR_REP', 'Represents vendor companies, manages vendor performance, and handles supplier communications'),
	(5, 'CUSTOMER', 'End customer portal access for tracking orders, shipments, and managing deliveries');

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

-- Dumping data for table globaltrade_logistics_corporation.route_plans: ~6 rows (approximately)
INSERT INTO `route_plans` (`route_id`, `shipment_id`, `planned_route`, `optimized_route`, `distance_km`, `estimated_time_hours`, `calculated_at`) VALUES
	(1, 1, 'It starts with a sea voyage from Los Angeles through the Panama Canal to New York, continuing across the Atlantic Ocean to Le Havre, France. The final leg finishes with road freight via truck from Le Havre to Paris', NULL, 14000.00, 466.67, '2026-09-01 13:27:44'),
	(2, 1, 'It starts with a sea voyage from Los Angeles through the Panama Canal to New York, continuing across the Atlantic Ocean to Le Havre, France. The final leg finishes with road freight via truck from Le Havre to Paris', 'The voyage goes from Los Angeles through the Panama Canal and straight across the Atlantic to Le Havre, France, totaling 12,600 km by sea. The final leg remains a 200 km road trip via truck from Le Havre to Paris.', 12800.00, 426.67, '2026-09-01 13:30:12'),
	(3, 2, 'Sea freight from Rotterdam through the English Channel to Dover, UK, followed by road transport via truck from Dover to London', NULL, 520.00, 10.40, '2026-09-01 13:43:14'),
	(4, 2, 'Sea freight from Rotterdam through the English Channel to Dover, UK, followed by road transport via truck from Dover to London', 'Sea freight directly from Rotterdam to Felixstowe, UK, followed by road transport via truck from Felixstowe to London, avoiding Dover congestion', 450.00, 9.00, '2026-09-01 13:43:36'),
	(5, 3, 'Sea freight from Sydney through the Suez Canal to Southampton, UK, followed by road transport to London. Then sea freight from London through the Suez Canal to Busan, South Korea, followed by road transport to Seoul', NULL, 19800.00, 396.00, '2026-09-01 13:48:41'),
	(6, 3, 'Sea freight from Sydney through the Suez Canal to Southampton, UK, followed by road transport to London. Then sea freight from London through the Suez Canal to Busan, South Korea, followed by road transport to Seoul', 'Sea freight from Sydney directly to Singapore for consolidation, then split: one shipment continues by sea through the Suez Canal to Southampton then road to London, while the other continues by sea through the South China Sea to Busan then road to Seoul', 17350.00, 347.00, '2026-09-01 13:49:04'),
	(7, 4, 'Air freight from Los Angeles International Airport to Incheon International Airport, South Korea, followed by road transport via truck from Incheon to Seoul', NULL, 9650.00, 193.00, '2026-09-01 13:54:13');

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

-- Dumping data for table globaltrade_logistics_corporation.shipments: ~4 rows (approximately)
INSERT INTO `shipments` (`shipment_id`, `tracking_number`, `origin_warehouse_id`, `destination`, `vendor_id`, `carrier_id`, `status`, `scheduled_date`, `estimated_delivery_date`, `actual_delivery_date`, `created_by`, `created_at`) VALUES
	(1, 'LAM-MCG-SFA-260901185744', 1, '15 Avenue des Champs-ÃÂlysÃÂ©es, Paris 75008, France', 6, 12, 'DELIVERED', '2026-09-02 08:00:00', '2026-09-20 08:00:00', '2026-09-01 19:28:35', 1, '2026-09-01 13:27:44'),
	(2, 'REG-ETR-SEA-260901191313', 2, '10 Downing Street, London SW1A 2AA, UK', 5, 13, 'DELIVERED', '2026-09-02 08:00:00', '2026-09-03 08:00:00', '2026-09-01 19:28:42', 1, '2026-09-01 13:43:14'),
	(3, 'SOD-TLA-SEA-260901191840', 6, '1)221B Baker Street, London NW1 6XE, UK 28-3   2)Myeongdong 8-gil, Jung-gu, Seoul, South Korea', 2, 13, 'IN_TRANSIT', '2026-09-02 20:00:00', '2026-09-17 20:00:00', NULL, 1, '2026-09-01 13:48:41'),
	(4, 'LAM-GFE-AIR-260901192412', 1, '15 Myeongdong-gil, Jung-gu, Seoul, South Korea', 1, 2, 'IN_TRANSIT', '2026-09-02 08:00:00', '2026-09-11 08:00:00', NULL, 1, '2026-09-01 13:54:13');

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

-- Dumping data for table globaltrade_logistics_corporation.shipment_items: ~8 rows (approximately)
INSERT INTO `shipment_items` (`id`, `shipment_id`, `item_id`, `quantity`) VALUES
	(1, 1, 1, 75),
	(2, 1, 2, 40),
	(3, 2, 6, 60),
	(4, 2, 5, 35),
	(5, 3, 18, 35),
	(6, 3, 17, 35),
	(7, 3, 16, 25),
	(8, 4, 3, 25),
	(9, 4, 2, 10);

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

-- Dumping data for table globaltrade_logistics_corporation.shipment_orders: ~4 rows (approximately)
INSERT INTO `shipment_orders` (`id`, `shipment_id`, `order_id`) VALUES
	(1, 1, 8),
	(2, 2, 5),
	(3, 3, 2),
	(4, 3, 9),
	(5, 4, 4);

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

-- Dumping data for table globaltrade_logistics_corporation.shipment_status_history: ~8 rows (approximately)
INSERT INTO `shipment_status_history` (`history_id`, `shipment_id`, `old_status`, `new_status`, `changed_by`, `changed_at`, `remarks`) VALUES
	(1, 1, NULL, 'PENDING', 1, '2026-09-01 13:27:44', 'Shipment created'),
	(2, 1, 'PENDING', 'IN_TRANSIT', 1, '2026-09-01 13:27:52', ''),
	(3, 2, NULL, 'PENDING', 1, '2026-09-01 13:43:14', 'Shipment created'),
	(4, 2, 'PENDING', 'IN_TRANSIT', 1, '2026-09-01 13:44:03', ''),
	(5, 3, NULL, 'IN_TRANSIT', 1, '2026-09-01 13:48:41', 'Shipment created'),
	(6, 4, NULL, 'IN_TRANSIT', 1, '2026-09-01 13:54:13', 'Shipment created'),
	(7, 1, 'IN_TRANSIT', 'DELIVERED', 1, '2026-09-01 13:58:35', ''),
	(8, 2, 'IN_TRANSIT', 'DELIVERED', 1, '2026-09-01 13:58:42', '');

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

-- Dumping data for table globaltrade_logistics_corporation.system_timers: ~3 rows (approximately)
INSERT INTO `system_timers` (`timer_id`, `timer_name`, `timer_type`, `schedule_expression`, `last_run`, `next_run`, `status`) VALUES
	(1, 'Inventory Reorder Check', 'DECLARATIVE', 'Schedule', '2026-09-02 14:00:00', '2026-09-02 14:01:00', 'ACTIVE'),
	(2, 'Customs Deadline Check', 'DECLARATIVE', 'Schedule', '2026-09-02 14:00:00', '2026-09-02 14:01:00', 'ACTIVE'),
	(3, 'Shipment Delay Check', 'DECLARATIVE', 'Schedule', '2026-09-02 14:00:00', '2026-09-02 14:01:00', 'ACTIVE');

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

-- Dumping data for table globaltrade_logistics_corporation.users: ~11 rows (approximately)
INSERT INTO `users` (`user_id`, `username`, `password_hash`, `email`, `full_name`, `role_id`, `vendor_id`, `status`, `created_at`) VALUES
	(1, 'maverick', 'cXNoOxmnBOc9rbFzn/dOZs7Fspa24GWxy+N8g12agZA=', 'tom.cruise@gmail.com', 'Tom Cruise', 1, NULL, 'ACTIVE', '2026-09-01 06:27:43'),
	(2, 'blackwidow', 'fAMwalctgGcppiXRru2BSQe0MevRns4Cq3i5aN16fxM=', 'scarlett.johansson@gmail.com', 'Scarlett Johansson', 3, NULL, 'ACTIVE', '2026-09-01 07:22:40'),
	(3, 'yelena', 'ghBg/g/TG+7Q2rMGBljr5f6XOBopGrh0YKgbPJH1Ytg=', 'florence.pugh@gmail.com', 'Florence Pugh', 1, NULL, 'ACTIVE', '2026-09-01 07:23:33'),
	(4, 'cptamerica', '7wqZx34ujfXSRYPHPAY09t+Gk+c9aqxZxEhyOlC//xE=', 'chris.evans@gmail.com', 'Chris Evans', 4, 1, 'ACTIVE', '2026-09-01 07:35:48'),
	(5, 'littleangel', 'gPq5P9pumG2HTcKuUO9GLnim9z2Uj3VZ40GEWhLDPTE=', 'kim.sohyun@gmail.com', 'Kim So Hyun', 5, NULL, 'ACTIVE', '2026-09-01 07:36:28'),
	(6, 'scarletwitch', 'p35OWlQ5QYrPWMjkSIlZegyTB2RWbV3T6v3LRgc6JuU=', 'elisabeth.olsen@gmail.com', 'Elisabeth Olsen', 2, NULL, 'PENDING_APPROVAL', '2026-09-01 07:37:24'),
	(7, 'piratequeen', 'qKHYpPt+iuTsF6brXtR0P/pdfopldoyc569uiwmuCQE=', 'keira.knightley@gmail.com', 'Keira Knightley', 5, NULL, 'ACTIVE', '2026-09-01 07:38:39'),
	(8, 'captainjacksparrow', 'p3xkl+tGUFxCL9N0yQ19G2XIUbboIT1HxQWxlGo3NWc=', 'johnny.depp@gmail.com', 'Johnny Depp', 4, 2, 'ACTIVE', '2026-09-01 07:39:22'),
	(9, 'Daenerys', 'o4/am3yhv3xFB8rZC2OtbbYke8HWhM7qzpHOnZ92gZw=', 'emilia.clarke@gmail.com', 'Emilia Clarke', 5, NULL, 'ACTIVE', '2026-09-01 07:42:15'),
	(10, 'bestflash', 'upGMJHS3Sle1HlOIn5XTiIgdK6gh0sxZLCaoeEoS3+w=', 'grant.gustin@gmail.com', 'Grant Gustin', 4, 3, 'ACTIVE', '2026-09-01 07:43:07'),
	(11, 'ironman', '4T4Nt13Yvk4MKv1IcCIWQgMq3L4jW30KubvYNy2I4+0=', 'robert.downeyjr@gmail.com', 'Robert Downey Jr', 2, NULL, 'ACTIVE', '2026-09-01 07:43:52');

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

-- Dumping data for table globaltrade_logistics_corporation.vendors: ~10 rows (approximately)
INSERT INTO `vendors` (`vendor_id`, `vendor_name`, `contact_email`, `contact_phone`, `country`, `performance_rating`, `status`, `created_at`) VALUES
	(1, 'Global Freight Express', 'contact@globalfreight.com', '+1-212-555-0101', 'US', 4.40, 'ACTIVE', '2026-09-01 07:28:56'),
	(2, 'TransOcean Logistics', 'info@transoceanlogistics.com', '+44-20-7946-0202', 'UK', 4.50, 'ACTIVE', '2026-09-01 07:28:56'),
	(3, 'SkyBridge Carriers', 'operations@skybridgecarriers.com', '+49-30-1234-5678', 'Germany', 4.70, 'ACTIVE', '2026-09-01 07:28:56'),
	(4, 'Pacific Rim Shipping', 'support@pacificrim.com', '+81-3-1234-5678', 'Japan', 4.30, 'ACTIVE', '2026-09-01 07:28:56'),
	(5, 'EuroLink Transport', 'dispatch@eurolink.com', '+33-1-2345-6789', 'France', 4.60, 'ACTIVE', '2026-09-01 07:28:56'),
	(6, 'MegaLogistics Corp', 'info@megalogistics.com', '+61-2-9876-5432', 'Australia', 4.90, 'ACTIVE', '2026-09-01 07:28:56'),
	(7, 'SwiftArrow Delivery', 'contact@swiftarrow.com', '+1-310-555-0202', 'US', 4.20, 'ACTIVE', '2026-09-01 07:28:56'),
	(8, 'Apex Cargo Solutions', 'operations@apexcargo.com', '+65-6789-0123', 'Singapore', 4.75, 'ACTIVE', '2026-09-01 07:28:56'),
	(9, 'Nordic Freight Alliance', 'info@nordicfreight.com', '+46-8-123-4567', 'Sweden', 4.40, 'ACTIVE', '2026-09-01 07:28:56'),
	(10, 'PanGlobal Logistics', 'dispatch@panglobal.com', '+86-10-5678-9012', 'China', 4.55, 'ACTIVE', '2026-09-01 07:28:56'),
	(11, 'IslandLink Logistics', 'info@islandlink.com', '+94-11-234-5678', 'Sri Lanka', 2.80, 'SUSPENDED', '2026-09-01 11:40:40');

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

-- Dumping data for table globaltrade_logistics_corporation.vendor_performance_evaluations: ~10 rows (approximately)
INSERT INTO `vendor_performance_evaluations` (`eval_id`, `vendor_id`, `evaluator_id`, `score`, `evaluation_date`, `comments`) VALUES
	(1, 1, 1, 4.80, '2026-08-31', 'Excellent performance. Global Freight Express consistently delivers on time with minimal delays. Strong communication and tracking capabilities. Highly recommended for international shipments.'),
	(2, 2, 1, 4.50, '2026-09-01', 'Very reliable service. TransOcean Logistics handles complex routes well. Slight room for improvement in customs documentation processing. Overall solid performer.'),
	(3, 3, 1, 4.70, '2026-09-01', 'Outstanding carrier. SkyBridge Carriers provides exceptional air freight services. Professional team, excellent tracking system, and proactive issue resolution. Near-perfect performance.'),
	(4, 4, 1, 4.30, '2026-09-01', 'Good performance with occasional delays during peak seasons. Pacific Rim Shipping excels in sea freight but could improve land-based logistics. Reliable partner overall.'),
	(5, 5, 1, 4.60, '2026-09-01', 'Strong performer. EuroLink Transport has excellent European network coverage. Timely deliveries and responsive customer service. Minor improvements needed in real-time tracking updates.'),
	(6, 6, 1, 4.90, '2026-09-01', 'Top-tier performer. MegaLogistics Corp exceeds expectations in all categories. Exceptional service quality, cutting-edge technology integration, and outstanding customer support. Industry benchmark.'),
	(7, 7, 1, 4.20, '2026-09-01', 'Satisfactory performance. SwiftArrow Delivery handles domestic routes well. Some delays observed on international shipments. Good communication but needs to improve multi-modal coordination.'),
	(8, 8, 1, 4.75, '2026-09-01', 'Excellent partner. Apex Cargo Solutions demonstrates strong operational excellence in Asian markets. Fast response times and efficient handling of complex shipments. Highly professional.'),
	(9, 9, 1, 4.40, '2026-09-01', 'Good service provider. Nordic Freight Alliance performs well in European routes. Reliable and consistent delivery schedules. Could enhance digital tracking capabilities for better visibility.'),
	(10, 10, 1, 4.55, '2026-09-01', 'Solid performance. PanGlobal Logistics offers comprehensive coverage across Asian markets. Competitive pricing and reasonable delivery times. Recommended for regional distribution.'),
	(11, 11, 1, 2.80, '2026-09-01', 'Frequent delivery delays and poor client communication. Tracking is often inaccurate. Missed customs deadlines regularly. Needs major improvement.'),
	(12, 1, 1, 4.00, '2026-09-01', 'Significant complains about delivery personnal character\r\n');

-- Dumping structure for table globaltrade_logistics_corporation.warehouses
CREATE TABLE IF NOT EXISTS `warehouses` (
  `warehouse_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `location` varchar(150) NOT NULL,
  `country` varchar(60) NOT NULL,
  `capacity` int NOT NULL,
  PRIMARY KEY (`warehouse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table globaltrade_logistics_corporation.warehouses: ~10 rows (approximately)
INSERT INTO `warehouses` (`warehouse_id`, `name`, `location`, `country`, `capacity`) VALUES
	(1, 'Los Angeles Mega Hub', '1234 Harbor Blvd, Los Angeles, CA 90001', 'USA', 50000),
	(2, 'Rotterdam European Gateway', 'Port of Rotterdam, Rotterdam 3011', 'Netherlands', 45000),
	(3, 'Singapore Southeast Asia Hub', '88 Airport Logistics Park, Singapore 819999', 'Singapore', 35000),
	(4, 'Shanghai Pacific Center', '2000 Pudong Airport Road, Shanghai 201202', 'China', 55000),
	(5, 'Dubai Middle East Logistics', 'Jebel Ali Free Zone, Dubai 00000', 'UAE', 40000),
	(6, 'Sydney Oceania Distribution', '1 Sydney Harbour Drive, Sydney NSW 2000', 'Australia', 30000),
	(7, 'Hamburg Central Warehouse', 'Hamburger Hafen, Hamburg 20457', 'Germany', 42000),
	(8, 'New York East Coast Hub', '500 Port Elizabeth, Newark, NJ 07114', 'United States', 38000),
	(9, 'Tokyo Pacific Logistics', '1-1 Tokyo Port, Tokyo 135-0064', 'Japan', 32000),
	(10, 'London Thames Gateway', 'London Gateway Port, Stanford-le-Hope SS17 9PD', 'United Kingdom', 36000);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
