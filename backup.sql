-- MySQL dump 10.13  Distrib 8.0.44, for Linux (x86_64)
--
-- Host: localhost    Database: gasmanager_users
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Current Database: `gasmanager_users`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `gasmanager_users` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `gasmanager_users`;

--
-- Table structure for table `auditoria_accion`
--

DROP TABLE IF EXISTS `auditoria_accion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria_accion` (
  `id_auditoria` int NOT NULL AUTO_INCREMENT,
  `datos_anteriores` tinytext,
  `datos_nuevos` tinytext,
  `descripcion` varchar(255) DEFAULT NULL,
  `fecha_hora` datetime(6) DEFAULT NULL,
  `id_usuario_ejecutor` int DEFAULT NULL,
  `modulo_afectado` varchar(255) DEFAULT NULL,
  `origen` varchar(255) DEFAULT NULL,
  `tipo_acccion` enum('ACTUALIZAR','CREAR','ELIMINAR','LEER','VALIDAR') DEFAULT NULL,
  PRIMARY KEY (`id_auditoria`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditoria_accion`
--

LOCK TABLES `auditoria_accion` WRITE;
/*!40000 ALTER TABLE `auditoria_accion` DISABLE KEYS */;
INSERT INTO `auditoria_accion` VALUES (1,NULL,'{\"ultimoAcceso\":\"2026-07-25T17:34:18.455764565\",\"rol\":\"ADMIN\"}','Login exitoso','2026-07-25 11:34:18.456123',1,'Login','WEB','VALIDAR'),(2,NULL,'{\"ultimoAcceso\":\"2026-07-25T18:49:47.148282026\",\"rol\":\"ADMIN\"}','Login exitoso','2026-07-25 12:49:47.149164',1,'Login','WEB','VALIDAR'),(3,NULL,'{\"ultimoAcceso\":\"2026-07-26T03:00:50.054495926\",\"rol\":\"ADMIN\"}','Login exitoso','2026-07-25 21:00:50.054905',1,'Login','WEB','VALIDAR'),(4,NULL,'{\"ultimoAcceso\":\"2026-07-26T03:25:20.390431141\",\"rol\":\"ADMIN\"}','Login exitoso','2026-07-25 21:25:20.390468',1,'Login','WEB','VALIDAR'),(5,NULL,'{\"ultimoAcceso\":\"2026-07-26T03:25:56.938662891\",\"rol\":\"ADMIN\"}','Login exitoso','2026-07-25 21:25:56.938703',1,'Login','WEB','VALIDAR'),(6,NULL,'{\"ultimoAcceso\":\"2026-07-26T12:10:29.669066426\",\"rol\":\"ADMIN\"}','Login exitoso','2026-07-26 06:10:29.669369',1,'Login','WEB','VALIDAR'),(7,NULL,'{\"ultimoAcceso\":\"2026-07-27T16:51:14.946343078\",\"rol\":\"ADMIN\"}','Login exitoso','2026-07-27 10:51:14.946381',1,'Login','WEB','VALIDAR'),(8,NULL,'{\"ultimoAcceso\":\"2026-07-27T21:57:49.447835082\",\"rol\":\"ADMIN\"}','Login exitoso','2026-07-27 15:57:49.448291',1,'Login','WEB','VALIDAR'),(9,NULL,'{\"nombre\":\"Usuario de Prueba\",\"correo\":\"exampleUsuario@example.com\",\"rol\":\"null\"}','Usuario creado exitosamente','2026-08-03 08:32:35.038609',2,'Usuarios','Sistema','CREAR'),(10,NULL,'{\"ultimoAcceso\":\"2026-08-03T14:32:48.082030275\",\"rol\":\"ADMIN\"}','Login exitoso','2026-08-03 08:32:48.082330',2,'Login','WEB','VALIDAR'),(11,'{\"nombre\":\"Administrador del Sistema\",\"rol\":\"ADMIN\",\"estado\":\"ACTIVO\",\"activo\":true}','{\"nombre\":\"Administrador del Sistema\",\"rol\":\"null\",\"estado\":\"ACTIVO\",\"activo\":true}','Usuario actualizado','2026-08-03 08:52:53.501908',1,'Usuarios','Sistema','ACTUALIZAR'),(12,NULL,'{\"ultimoAcceso\":\"2026-08-03T14:53:51.937898651\",\"rol\":\"ADMIN\"}','Login exitoso','2026-08-03 08:53:51.937930',1,'Login','WEB','VALIDAR');
/*!40000 ALTER TABLE `auditoria_accion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_token`
--

DROP TABLE IF EXISTS `password_reset_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_token` (
  `id_token` int NOT NULL AUTO_INCREMENT,
  `fecha_expiracion` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `usado` bit(1) NOT NULL,
  `id_usuario` int NOT NULL,
  PRIMARY KEY (`id_token`),
  UNIQUE KEY `UK_g0guo4k8krgpwuagos61oc06j` (`token`),
  UNIQUE KEY `UK_johu5tq9i7cy1fgyemmlme0p2` (`id_usuario`),
  CONSTRAINT `FKno4ngi2ecktio49ytrq5d2cxh` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_token`
--

LOCK TABLES `password_reset_token` WRITE;
/*!40000 ALTER TABLE `password_reset_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permiso`
--

DROP TABLE IF EXISTS `permiso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permiso` (
  `id_permiso` int NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `codigo_permiso` varchar(255) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `nombre_permiso` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_permiso`),
  UNIQUE KEY `UK_rvbnkgjp2581y1hndb2sqyx5g` (`codigo_permiso`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permiso`
--

LOCK TABLES `permiso` WRITE;
/*!40000 ALTER TABLE `permiso` DISABLE KEYS */;
INSERT INTO `permiso` VALUES (1,_binary '','USUARIO_CREAR','Permite crear nuevos usuarios','2026-07-25 11:17:34.737052','Crear Usuario'),(2,_binary '','USUARIO_LEER','Permite ver usuarios','2026-07-25 11:17:34.737064','Leer Usuario'),(3,_binary '','USUARIO_ACTUALIZAR','Permite modificar usuarios','2026-07-25 11:17:34.737068','Actualizar Usuario'),(4,_binary '','USUARIO_ELIMINAR','Permite desactivar usuarios','2026-07-25 11:17:34.737078','Eliminar Usuario'),(5,_binary '','ROL_CREAR','Permite crear roles','2026-07-25 11:17:34.737083','Crear Rol'),(6,_binary '','ROL_LEER','Permite ver roles','2026-07-25 11:17:34.737086','Leer Rol'),(7,_binary '','ROL_ACTUALIZAR','Permite modificar roles','2026-07-25 11:17:34.737090','Actualizar Rol'),(8,_binary '','ROL_ELIMINAR','Permite eliminar roles','2026-07-25 11:17:34.737093','Eliminar Rol'),(9,_binary '','PERMISO_CREAR','Permite crear permisos','2026-07-25 11:17:34.737096','Crear Permiso'),(10,_binary '','PERMISO_LEER','Permite ver permisos','2026-07-25 11:17:34.737098','Leer Permiso'),(11,_binary '','AUDITORIA_LEER','Permite ver registros de auditoria','2026-07-25 11:17:34.737101','Leer Auditoria');
/*!40000 ALTER TABLE `permiso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `id_rol` int NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `nombre_rol` varchar(255) NOT NULL,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `UK_l0qdsam7tunbtmxcmeeyfcifk` (`nombre_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
INSERT INTO `rol` VALUES (1,_binary '','Administrador del sistema con todos los permisos','2026-07-25 11:17:35.189553','ADMIN'),(2,_binary '','Usuario estandar con permisos basicos','2026-07-25 11:17:35.297086','USUARIO'),(3,_binary '','Usuario con permisos solo de auditoria','2026-07-25 11:17:35.368331','AUDITOR');
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles_permisos`
--

DROP TABLE IF EXISTS `roles_permisos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles_permisos` (
  `id_rol` int NOT NULL,
  `id_permiso` int NOT NULL,
  PRIMARY KEY (`id_rol`,`id_permiso`),
  KEY `FK9rnwty0nf6dley4emnr6lk175` (`id_permiso`),
  CONSTRAINT `FK9rnwty0nf6dley4emnr6lk175` FOREIGN KEY (`id_permiso`) REFERENCES `permiso` (`id_permiso`),
  CONSTRAINT `FKode9508gq58igyq0crn5xp75b` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles_permisos`
--

LOCK TABLES `roles_permisos` WRITE;
/*!40000 ALTER TABLE `roles_permisos` DISABLE KEYS */;
INSERT INTO `roles_permisos` VALUES (1,1),(1,2),(2,2),(3,2),(1,3),(2,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(3,11);
/*!40000 ALTER TABLE `roles_permisos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sesion_usuario`
--

DROP TABLE IF EXISTS `sesion_usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sesion_usuario` (
  `id_sesion` int NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `fecha_expiracion` datetime(6) DEFAULT NULL,
  `fecha_inicio` datetime(6) DEFAULT NULL,
  `id_usuario` int DEFAULT NULL,
  `origen` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `ultima_actividad` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id_sesion`),
  UNIQUE KEY `UK_tc95lwuhv8smqio6jxuim8vr1` (`token`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sesion_usuario`
--

LOCK TABLES `sesion_usuario` WRITE;
/*!40000 ALTER TABLE `sesion_usuario` DISABLE KEYS */;
INSERT INTO `sesion_usuario` VALUES (1,_binary '','2026-07-25 13:34:18.748991','2026-07-25 11:34:18.748983',1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnYXNtYW5hZ2VyLmNvbSIsImlkVXN1YXJpbyI6MSwicm9sIjoiQURNSU4iLCJpYXQiOjE3ODUwMDA4NTgsImV4cCI6MTc4NTAwNDQ1OH0.b2_EyO6T33HP4P4o9JxSXZvWJipLRK4G-bhK4Yr4k10',NULL),(2,_binary '','2026-07-25 14:49:47.417751','2026-07-25 12:49:47.417745',1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnYXNtYW5hZ2VyLmNvbSIsImlkVXN1YXJpbyI6MSwicm9sIjoiQURNSU4iLCJpYXQiOjE3ODUwMDUzODcsImV4cCI6MTc4NTAwODk4N30.RvSTK91Oa62wTzohhw3TpGTL1JO5h9h_jsNLTZMwEmA',NULL),(3,_binary '','2026-07-25 23:00:50.650561','2026-07-25 21:00:50.650553',1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnYXNtYW5hZ2VyLmNvbSIsImlkVXN1YXJpbyI6MSwicm9sIjoiQURNSU4iLCJpYXQiOjE3ODUwMzQ4NTAsImV4cCI6MTc4NTAzODQ1MH0.d2l8e3WW1-DfLH241g0M_bNYTVM5Ed8mrEU6rP5Ru8A',NULL),(4,_binary '','2026-07-25 23:25:20.442550','2026-07-25 21:25:20.442545',1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnYXNtYW5hZ2VyLmNvbSIsImlkVXN1YXJpbyI6MSwicm9sIjoiQURNSU4iLCJpYXQiOjE3ODUwMzYzMjAsImV4cCI6MTc4NTAzOTkyMH0.VchaW-_58Y_B_upCIbsmqWvou3Rt6hOkjRUK_HQkKY0',NULL),(5,_binary '','2026-07-25 23:25:57.007806','2026-07-25 21:25:57.007801',1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnYXNtYW5hZ2VyLmNvbSIsImlkVXN1YXJpbyI6MSwicm9sIjoiQURNSU4iLCJpYXQiOjE3ODUwMzYzNTcsImV4cCI6MTc4NTAzOTk1N30.GJ3vUDtq2WFGR4EfdQChfg6srw9gQoeDwF-knxxpPDk',NULL),(6,_binary '','2026-07-26 08:10:29.927436','2026-07-26 06:10:29.927419',1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnYXNtYW5hZ2VyLmNvbSIsImlkVXN1YXJpbyI6MSwicm9sIjoiQURNSU4iLCJpYXQiOjE3ODUwNjc4MjksImV4cCI6MTc4NTA3MTQyOX0.SwJR5C177ZyW9b-3tREJzT3x_rjmlVFe1A2yrWJ7XY4',NULL),(7,_binary '','2026-07-27 12:51:15.025976','2026-07-27 10:51:15.025969',1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnYXNtYW5hZ2VyLmNvbSIsImlkVXN1YXJpbyI6MSwicm9sIjoiQURNSU4iLCJpYXQiOjE3ODUxNzEwNzUsImV4cCI6MTc4NTE3NDY3NX0.L3QgnNTtS467A9ordFKgnwmNNESycSGEfcxgFySdMBo',NULL),(8,_binary '','2026-07-27 17:57:49.718464','2026-07-27 15:57:49.718457',1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnYXNtYW5hZ2VyLmNvbSIsImlkVXN1YXJpbyI6MSwicm9sIjoiQURNSU4iLCJpYXQiOjE3ODUxODk0NjksImV4cCI6MTc4NTE5MzA2OX0.0gSSpnYzQzluPUeIdFNRn3hJ6Ui9uR-6l7iQhjwruFo',NULL),(9,_binary '','2026-08-03 10:32:48.126485','2026-08-03 08:32:48.126480',2,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJleGFtcGxlVXN1YXJpb0BleGFtcGxlLmNvbSIsImlkVXN1YXJpbyI6Miwicm9sIjoiQURNSU4iLCJpYXQiOjE3ODU3Njc1NjgsImV4cCI6MTc4NTc3MTE2OH0.h1I-_Yl0PjL-J5Ws-7Icu63nHyN3c-TcC4NnZelE8Kc',NULL),(10,_binary '','2026-08-03 10:53:51.949107','2026-08-03 08:53:51.949103',1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnYXNtYW5hZ2VyLmNvbSIsImlkVXN1YXJpbyI6MSwicm9sIjoiQURNSU4iLCJpYXQiOjE3ODU3Njg4MzEsImV4cCI6MTc4NTc3MjQzMX0.SWx3e-nLgjYtG2ngGf0u1hWTHxxG4FsgsI9JThkN1zs',NULL);
/*!40000 ALTER TABLE `sesion_usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `bloqueado` bit(1) DEFAULT NULL,
  `correo` varchar(255) NOT NULL,
  `estado` enum('ACTIVO','BLOQUEADO','INACTIVO','SUSPENDIDO') DEFAULT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `intentos_fallidos` int DEFAULT NULL,
  `nombre` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `ultimo_acceso` datetime(6) DEFAULT NULL,
  `rol_id` int DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `UK_2mlfr087gb1ce55f2j87o74t` (`correo`),
  KEY `FKshkwj12wg6vkm6iuwhvcfpct8` (`rol_id`),
  CONSTRAINT `FKshkwj12wg6vkm6iuwhvcfpct8` FOREIGN KEY (`rol_id`) REFERENCES `rol` (`id_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,_binary '',_binary '\0','admin@gasmanager.com','ACTIVO','2026-07-25 11:17:35.599315',0,'Administrador del Sistema','$2b$10$7djNX4c9/SnrljvglfqWxObBt64T4MjIXsCfCfBLzmQhXscsy6SmS','2026-08-03 08:53:51.827551',1),(2,_binary '',_binary '\0','exampleUsuario@example.com','ACTIVO','2026-08-03 08:32:34.775304',0,'Usuario de Prueba','$2a$10$QbB5UA0kzPLI19PFCgPVAe1qFW359yuM9b4GrJ4ia44GZ/6Z9zOtm','2026-08-03 08:32:47.817860',1);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'gasmanager_users'
--

--
-- Dumping routines for database 'gasmanager_users'
--

--
-- Current Database: `gasmanager_ventas`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `gasmanager_ventas` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `gasmanager_ventas`;

--
-- Table structure for table `caras_dispensario`
--

DROP TABLE IF EXISTS `caras_dispensario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `caras_dispensario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `codigo` varchar(10) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `dispensario_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9mvujkd0usevatxp3cm3by8kg` (`dispensario_id`),
  CONSTRAINT `FK9mvujkd0usevatxp3cm3by8kg` FOREIGN KEY (`dispensario_id`) REFERENCES `dispensarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `caras_dispensario`
--

LOCK TABLES `caras_dispensario` WRITE;
/*!40000 ALTER TABLE `caras_dispensario` DISABLE KEYS */;
INSERT INTO `caras_dispensario` VALUES (1,_binary '','A','Cara A',1),(2,_binary '','B','Cara B',1);
/*!40000 ALTER TABLE `caras_dispensario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cortes_turno`
--

DROP TABLE IF EXISTS `cortes_turno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cortes_turno` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `codigo_corte` varchar(30) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `diferencia_efectivo` decimal(10,2) DEFAULT NULL,
  `diferencia_inventario` decimal(10,3) DEFAULT NULL,
  `estado` enum('CERRADO','CON_DIFERENCIAS','PENDIENTE','RECHAZADO','VALIDADO') DEFAULT NULL,
  `fecha_corte` datetime(6) NOT NULL,
  `fecha_validacion` datetime(6) DEFAULT NULL,
  `inventario_final_gasolina` decimal(10,3) DEFAULT NULL,
  `inventario_inicial_gasolina` decimal(10,3) DEFAULT NULL,
  `nombre` varchar(50) NOT NULL,
  `numero_ventas` int DEFAULT NULL,
  `observaciones` text,
  `reporte_excel_path` varchar(255) DEFAULT NULL,
  `reporte_pdf_path` varchar(255) DEFAULT NULL,
  `total_credito` decimal(12,2) DEFAULT NULL,
  `total_efectivo_real` decimal(12,2) DEFAULT NULL,
  `total_efectivo_reporte` decimal(12,2) DEFAULT NULL,
  `total_tarjeta` decimal(12,2) DEFAULT NULL,
  `total_transferencia` decimal(12,2) DEFAULT NULL,
  `total_ventas` decimal(12,2) DEFAULT NULL,
  `turno_codigo` varchar(20) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `validado_nombre` varchar(100) DEFAULT NULL,
  `validado_por` bigint DEFAULT NULL,
  `ventas_gasolina` decimal(10,3) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `turno_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jeyla1tx6c5psdah2bus7hia8` (`codigo_corte`),
  KEY `FKcnw73wk7nve4exoldfx54p6ak` (`turno_id`),
  CONSTRAINT `FKcnw73wk7nve4exoldfx54p6ak` FOREIGN KEY (`turno_id`) REFERENCES `turnos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cortes_turno`
--

LOCK TABLES `cortes_turno` WRITE;
/*!40000 ALTER TABLE `cortes_turno` DISABLE KEYS */;
/*!40000 ALTER TABLE `cortes_turno` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cortes_turno_detallado`
--

DROP TABLE IF EXISTS `cortes_turno_detallado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cortes_turno_detallado` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `codigo_corte` varchar(30) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `despachador_id` bigint DEFAULT NULL,
  `despachador_nombre` varchar(100) DEFAULT NULL,
  `diesel_importe` decimal(12,2) DEFAULT NULL,
  `diesel_lectura_final` decimal(10,3) DEFAULT NULL,
  `diesel_lectura_inicial` decimal(10,3) DEFAULT NULL,
  `diesel_litros_vendidos` decimal(10,3) DEFAULT NULL,
  `diesel_precio` decimal(10,2) DEFAULT NULL,
  `diferencia` decimal(12,2) DEFAULT NULL,
  `dispensario_id` bigint DEFAULT NULL,
  `dispensario_nombre` varchar(100) DEFAULT NULL,
  `efectivo_que_debe_entregar` decimal(12,2) DEFAULT NULL,
  `estado` enum('CERRADO','CON_DIFERENCIAS','PENDIENTE','RECHAZADO','VALIDADO') DEFAULT NULL,
  `magna_importe` decimal(12,2) DEFAULT NULL,
  `magna_lectura_final` decimal(10,3) DEFAULT NULL,
  `magna_lectura_inicial` decimal(10,3) DEFAULT NULL,
  `magna_litros_vendidos` decimal(10,3) DEFAULT NULL,
  `magna_precio` decimal(10,2) DEFAULT NULL,
  `observaciones` text,
  `premium_importe` decimal(12,2) DEFAULT NULL,
  `premium_lectura_final` decimal(10,3) DEFAULT NULL,
  `premium_lectura_inicial` decimal(10,3) DEFAULT NULL,
  `premium_litros_vendidos` decimal(10,3) DEFAULT NULL,
  `premium_precio` decimal(10,2) DEFAULT NULL,
  `total_aceites_importe` decimal(12,2) DEFAULT NULL,
  `total_combustibles_importe` decimal(12,2) DEFAULT NULL,
  `total_combustibles_litros` decimal(10,3) DEFAULT NULL,
  `total_credito` decimal(12,2) DEFAULT NULL,
  `total_efectivo` decimal(12,2) DEFAULT NULL,
  `total_notas_credito` decimal(12,2) DEFAULT NULL,
  `total_tarjeta` decimal(12,2) DEFAULT NULL,
  `total_transferencia` decimal(12,2) DEFAULT NULL,
  `total_ventas` decimal(12,2) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `turno_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_8n57mcqwgf6l339hxkb90ipuw` (`codigo_corte`),
  KEY `FKp74rf4toansah6eoxam2bha7w` (`turno_id`),
  CONSTRAINT `FKp74rf4toansah6eoxam2bha7w` FOREIGN KEY (`turno_id`) REFERENCES `turnos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cortes_turno_detallado`
--

LOCK TABLES `cortes_turno_detallado` WRITE;
/*!40000 ALTER TABLE `cortes_turno_detallado` DISABLE KEYS */;
/*!40000 ALTER TABLE `cortes_turno_detallado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalles_aceite_corte`
--

DROP TABLE IF EXISTS `detalles_aceite_corte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalles_aceite_corte` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `aceite_id` bigint DEFAULT NULL,
  `aceite_nombre` varchar(100) DEFAULT NULL,
  `cantidad_final` int DEFAULT NULL,
  `cantidad_inicial` int DEFAULT NULL,
  `cantidad_vendida` int DEFAULT NULL,
  `importe` decimal(12,2) DEFAULT NULL,
  `precio_unitario` decimal(10,2) DEFAULT NULL,
  `corte_turno_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhj9jj1xw6ctcd1j240ysn7qh4` (`corte_turno_id`),
  CONSTRAINT `FKhj9jj1xw6ctcd1j240ysn7qh4` FOREIGN KEY (`corte_turno_id`) REFERENCES `cortes_turno_detallado` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalles_aceite_corte`
--

LOCK TABLES `detalles_aceite_corte` WRITE;
/*!40000 ALTER TABLE `detalles_aceite_corte` DISABLE KEYS */;
/*!40000 ALTER TABLE `detalles_aceite_corte` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalles_corte`
--

DROP TABLE IF EXISTS `detalles_corte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalles_corte` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comprobante_path` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `descripcion` varchar(200) NOT NULL,
  `diferencia` decimal(10,2) DEFAULT NULL,
  `monto_esperado` decimal(10,2) DEFAULT NULL,
  `monto_real` decimal(10,2) DEFAULT NULL,
  `observaciones` text,
  `referencia` varchar(50) DEFAULT NULL,
  `tipo` enum('AJUSTE_CAJA','AJUSTE_INVENTARIO','DEVOLUCION','DIFERENCIA_EFECTIVO','DIFERENCIA_INVENTARIO','FONDEO_INICIAL','GASTO_ALIMENTACION','GASTO_COMBUSTIBLE','GASTO_EXTRAORDINARIO','GASTO_MATERIALES','GASTO_TRANSPORTE','NOTA_CREDITO','PROPINA','VENTA_CREDITO','VENTA_EFECTIVO','VENTA_TARJETA','VENTA_TRANSFERENCIA') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `corte_turno_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1x5gqq5lbou1ao8xdmg6owq9y` (`corte_turno_id`),
  CONSTRAINT `FK1x5gqq5lbou1ao8xdmg6owq9y` FOREIGN KEY (`corte_turno_id`) REFERENCES `cortes_turno` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalles_corte`
--

LOCK TABLES `detalles_corte` WRITE;
/*!40000 ALTER TABLE `detalles_corte` DISABLE KEYS */;
/*!40000 ALTER TABLE `detalles_corte` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalles_venta`
--

DROP TABLE IF EXISTS `detalles_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalles_venta` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cantidad` decimal(10,3) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `importe` decimal(10,2) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `producto_codigo` varchar(50) DEFAULT NULL,
  `producto_id` bigint NOT NULL,
  `producto_nombre` varchar(100) NOT NULL,
  `tipo_producto` enum('ACEITE_MOTOR','ADITIVO','COMBUSTIBLE_DIESEL','COMBUSTIBLE_GASOLINA_MAGNA','COMBUSTIBLE_GASOLINA_PREMIUM','OTRO') NOT NULL,
  `unidad_medida` enum('CAJAS','GALONES','GRAMOS','KILOS','LITROS','METROS_CUBICOS','PIEZAS','UNIDADES') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `venta_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK453xcyfk9n6snv6qnjlo0p65p` (`venta_id`),
  CONSTRAINT `FK453xcyfk9n6snv6qnjlo0p65p` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalles_venta`
--

LOCK TABLES `detalles_venta` WRITE;
/*!40000 ALTER TABLE `detalles_venta` DISABLE KEYS */;
INSERT INTO `detalles_venta` VALUES (1,4.167,'2026-07-25 11:35:58.406533',100.00,24.00,NULL,1,'MAGNA','COMBUSTIBLE_GASOLINA_MAGNA','LITROS','2026-07-25 11:35:58.406547',1),(2,4.167,'2026-07-25 12:51:12.207427',100.00,24.00,NULL,1,'MAGNA','COMBUSTIBLE_GASOLINA_MAGNA','LITROS','2026-07-25 12:51:12.207446',2);
/*!40000 ALTER TABLE `detalles_venta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dispensarios`
--

DROP TABLE IF EXISTS `dispensarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dispensarios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `estado` enum('ACTIVO','CALIBRACION','FUERA_SERVICIO','INACTIVO','MANTENIMIENTO','SIN_COMBUSTIBLE') DEFAULT NULL,
  `lectura_actual` decimal(10,3) DEFAULT NULL,
  `lectura_inicial` decimal(10,3) DEFAULT NULL,
  `mangueras` int DEFAULT NULL,
  `nombre` varchar(50) NOT NULL,
  `numero` varchar(10) NOT NULL,
  `tiene_dos_caras` bit(1) DEFAULT NULL,
  `tipo_combustible` enum('DIESEL','ELECTRICO','GASOLINA_MAGNA','GASOLINA_PREMIUM','GAS_LP','HIBRIDO','OTRO') DEFAULT NULL,
  `ubicacion` varchar(100) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_cv7sd060b1q3yn7ullr1xfn8n` (`numero`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dispensarios`
--

LOCK TABLES `dispensarios` WRITE;
/*!40000 ALTER TABLE `dispensarios` DISABLE KEYS */;
INSERT INTO `dispensarios` VALUES (1,_binary '','2026-07-25 11:35:46.025418','ACTIVO',0.000,0.000,4,'Surtidor 1','01',_binary '',NULL,'Isla 1','2026-07-25 11:35:46.025434',0);
/*!40000 ALTER TABLE `dispensarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lecturas_base`
--

DROP TABLE IF EXISTS `lecturas_base`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lecturas_base` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `aceite_id` bigint DEFAULT NULL,
  `aceite_nombre` varchar(100) DEFAULT NULL,
  `cantidad_inicial` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `lectura_inicial` decimal(10,3) DEFAULT NULL,
  `manguera_id` bigint DEFAULT NULL,
  `manguera_nombre` varchar(50) DEFAULT NULL,
  `precio_por_litro` decimal(10,2) DEFAULT NULL,
  `precio_unitario` decimal(10,2) DEFAULT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  `tipo_combustible` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lecturas_base`
--

LOCK TABLES `lecturas_base` WRITE;
/*!40000 ALTER TABLE `lecturas_base` DISABLE KEYS */;
/*!40000 ALTER TABLE `lecturas_base` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lecturas_finales_turno`
--

DROP TABLE IF EXISTS `lecturas_finales_turno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lecturas_finales_turno` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `aceite_id` bigint DEFAULT NULL,
  `aceite_nombre` varchar(100) DEFAULT NULL,
  `cantidad_final` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `lectura_final` decimal(10,3) DEFAULT NULL,
  `manguera_id` bigint DEFAULT NULL,
  `manguera_nombre` varchar(50) DEFAULT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  `tipo_combustible` varchar(30) DEFAULT NULL,
  `turno_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKknngsvtf67aoko1l3ht2nfyie` (`turno_id`),
  CONSTRAINT `FKknngsvtf67aoko1l3ht2nfyie` FOREIGN KEY (`turno_id`) REFERENCES `turnos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lecturas_finales_turno`
--

LOCK TABLES `lecturas_finales_turno` WRITE;
/*!40000 ALTER TABLE `lecturas_finales_turno` DISABLE KEYS */;
/*!40000 ALTER TABLE `lecturas_finales_turno` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lecturas_iniciales_turno`
--

DROP TABLE IF EXISTS `lecturas_iniciales_turno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lecturas_iniciales_turno` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `aceite_id` bigint DEFAULT NULL,
  `aceite_nombre` varchar(100) DEFAULT NULL,
  `cantidad_inicial` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `lectura_inicial` decimal(10,3) DEFAULT NULL,
  `manguera_id` bigint DEFAULT NULL,
  `manguera_nombre` varchar(50) DEFAULT NULL,
  `precio_por_litro` decimal(10,2) DEFAULT NULL,
  `precio_unitario` decimal(10,2) DEFAULT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  `tipo_combustible` varchar(30) DEFAULT NULL,
  `turno_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKek37uhf8bmxmtpaic7c2823ol` (`turno_id`),
  CONSTRAINT `FKek37uhf8bmxmtpaic7c2823ol` FOREIGN KEY (`turno_id`) REFERENCES `turnos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lecturas_iniciales_turno`
--

LOCK TABLES `lecturas_iniciales_turno` WRITE;
/*!40000 ALTER TABLE `lecturas_iniciales_turno` DISABLE KEYS */;
/*!40000 ALTER TABLE `lecturas_iniciales_turno` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mangueras`
--

DROP TABLE IF EXISTS `mangueras`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mangueras` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `codigo` varchar(20) NOT NULL,
  `combustible_id` bigint DEFAULT NULL,
  `lectura_actual` decimal(10,3) DEFAULT NULL,
  `nombre` varchar(50) NOT NULL,
  `tipo_combustible` varchar(20) NOT NULL,
  `cara_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK685j8ro5pq8x1ftqmd39lh6ve` (`cara_id`),
  CONSTRAINT `FK685j8ro5pq8x1ftqmd39lh6ve` FOREIGN KEY (`cara_id`) REFERENCES `caras_dispensario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mangueras`
--

LOCK TABLES `mangueras` WRITE;
/*!40000 ALTER TABLE `mangueras` DISABLE KEYS */;
INSERT INTO `mangueras` VALUES (1,_binary '','A1',NULL,0.000,'Manguera A1','MAGNA',1),(2,_binary '','A2',NULL,0.000,'Manguera A2','PREMIUM',1),(3,_binary '','B1',NULL,0.000,'Manguera B1','MAGNA',2),(4,_binary '','B2',NULL,0.000,'Manguera B2','PREMIUM',2);
/*!40000 ALTER TABLE `mangueras` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notas_credito_corte`
--

DROP TABLE IF EXISTS `notas_credito_corte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notas_credito_corte` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `autorizado_por` varchar(100) DEFAULT NULL,
  `cliente_nombre` varchar(150) DEFAULT NULL,
  `folio_nota` varchar(50) NOT NULL,
  `litros` decimal(10,3) DEFAULT NULL,
  `monto` decimal(12,2) DEFAULT NULL,
  `tipo_combustible` varchar(30) DEFAULT NULL,
  `corte_turno_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK66xyvfv0pgw6vonaqrfej4ra8` (`corte_turno_id`),
  CONSTRAINT `FK66xyvfv0pgw6vonaqrfej4ra8` FOREIGN KEY (`corte_turno_id`) REFERENCES `cortes_turno_detallado` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notas_credito_corte`
--

LOCK TABLES `notas_credito_corte` WRITE;
/*!40000 ALTER TABLE `notas_credito_corte` DISABLE KEYS */;
/*!40000 ALTER TABLE `notas_credito_corte` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notas_credito_turno`
--

DROP TABLE IF EXISTS `notas_credito_turno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notas_credito_turno` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `autorizado_por` varchar(100) DEFAULT NULL,
  `cliente_id` bigint DEFAULT NULL,
  `cliente_nombre` varchar(150) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `folio_nota` varchar(50) NOT NULL,
  `litros` decimal(10,3) DEFAULT NULL,
  `monto` decimal(12,2) DEFAULT NULL,
  `observaciones` text,
  `tipo_combustible` varchar(30) DEFAULT NULL,
  `vehiculo_placas` varchar(20) DEFAULT NULL,
  `turno_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmdag8nrobtx0jr9gd48ln0sj0` (`turno_id`),
  CONSTRAINT `FKmdag8nrobtx0jr9gd48ln0sj0` FOREIGN KEY (`turno_id`) REFERENCES `turnos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notas_credito_turno`
--

LOCK TABLES `notas_credito_turno` WRITE;
/*!40000 ALTER TABLE `notas_credito_turno` DISABLE KEYS */;
/*!40000 ALTER TABLE `notas_credito_turno` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transacciones_dispensario`
--

DROP TABLE IF EXISTS `transacciones_dispensario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transacciones_dispensario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `estado` varchar(255) DEFAULT NULL,
  `fecha_hora` datetime(6) DEFAULT NULL,
  `litros` decimal(38,2) DEFAULT NULL,
  `surtidor_id` bigint DEFAULT NULL,
  `tipo_combustible` varchar(255) DEFAULT NULL,
  `total` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transacciones_dispensario`
--

LOCK TABLES `transacciones_dispensario` WRITE;
/*!40000 ALTER TABLE `transacciones_dispensario` DISABLE KEYS */;
/*!40000 ALTER TABLE `transacciones_dispensario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `turnos`
--

DROP TABLE IF EXISTS `turnos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `turnos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `codigo_turno` varchar(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `diferencia` decimal(10,2) DEFAULT NULL,
  `estado` enum('ABIERTO','AUDITADO','CANCELADO','CERRADO','CONCILIADO','PENDIENTE_VALIDACION') NOT NULL,
  `fecha_turno` datetime(6) NOT NULL,
  `hora_fin` time(6) DEFAULT NULL,
  `hora_inicio` time(6) NOT NULL,
  `litros_vendidos` decimal(10,3) DEFAULT NULL,
  `nombre` varchar(50) NOT NULL,
  `numero_clientes` int DEFAULT NULL,
  `numero_ventas` int DEFAULT NULL,
  `observaciones` text,
  `supervisor_id` bigint NOT NULL,
  `supervisor_nombre` varchar(100) DEFAULT NULL,
  `total_credito` decimal(12,2) DEFAULT NULL,
  `total_efectivo` decimal(12,2) DEFAULT NULL,
  `total_tarjeta` decimal(12,2) DEFAULT NULL,
  `total_transferencia` decimal(12,2) DEFAULT NULL,
  `total_ventas` decimal(12,2) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_dvi12roi1l7xqu9ndeaommufo` (`codigo_turno`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turnos`
--

LOCK TABLES `turnos` WRITE;
/*!40000 ALTER TABLE `turnos` DISABLE KEYS */;
INSERT INTO `turnos` VALUES (1,'TURNO-20260725-0001','2026-07-25 11:34:38.711998',NULL,NULL,'ABIERTO','2026-07-25 11:34:00.000000',NULL,'11:34:00.000000',NULL,'Turno 2',0,2,NULL,1,NULL,NULL,NULL,NULL,NULL,200.00,'2026-07-25 12:51:13.182418',NULL,2);
/*!40000 ALTER TABLE `turnos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ventas`
--

DROP TABLE IF EXISTS `ventas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ventas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cliente_id` bigint DEFAULT NULL,
  `cliente_nombre` varchar(150) DEFAULT NULL,
  `cliente_rfc` varchar(13) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `credito_id` bigint DEFAULT NULL,
  `despachador_id` bigint NOT NULL,
  `despachador_nombre` varchar(100) DEFAULT NULL,
  `dispensario_id` int DEFAULT NULL,
  `es_credito` bit(1) DEFAULT NULL,
  `estado` enum('CANCELADA','COMPLETADA','CREDITO_PENDIENTE','FACTURADA','PENDIENTE') NOT NULL,
  `facturada` bit(1) DEFAULT NULL,
  `fecha_hora` datetime(6) NOT NULL,
  `folio` varchar(50) NOT NULL,
  `folio_factura` varchar(50) DEFAULT NULL,
  `iva` decimal(10,2) NOT NULL,
  `metodo_pago` enum('CREDITO','EFECTIVO','PUNTOS_LEALTAD','TARJETA_CREDITO','TARJETA_DEBITO','TRANSFERENCIA') NOT NULL,
  `puntos_canjeados` int DEFAULT NULL,
  `puntos_obtenidos` int DEFAULT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `surtidor_id` int NOT NULL,
  `surtidor_numero` varchar(10) DEFAULT NULL,
  `total` decimal(10,2) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `turno_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_b5y0botwo90l4gfoixw3kjnhr` (`folio`),
  KEY `FKhs9hv6wnwe7lvxhjpvv2q1obm` (`turno_id`),
  CONSTRAINT `FKhs9hv6wnwe7lvxhjpvv2q1obm` FOREIGN KEY (`turno_id`) REFERENCES `turnos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ventas`
--

LOCK TABLES `ventas` WRITE;
/*!40000 ALTER TABLE `ventas` DISABLE KEYS */;
INSERT INTO `ventas` VALUES (1,NULL,NULL,NULL,'2026-07-25 11:35:58.394085',NULL,NULL,1,'SISTEMA',1,_binary '\0','COMPLETADA',_binary '\0','2026-07-25 11:35:58.394098','VENTA-20260725-173558-8392',NULL,13.79,'EFECTIVO',0,0,86.21,1,'A1',100.00,'2026-07-25 11:35:58.394102',NULL,0,1),(2,NULL,NULL,NULL,'2026-07-25 12:51:11.971892',NULL,NULL,1,'SISTEMA',1,_binary '\0','COMPLETADA',_binary '\0','2026-07-25 12:51:11.971978','VENTA-20260725-185111-1767',NULL,13.79,'EFECTIVO',0,0,86.21,1,'A1',100.00,'2026-07-25 12:51:11.971983',NULL,0,1);
/*!40000 ALTER TABLE `ventas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'gasmanager_ventas'
--

--
-- Dumping routines for database 'gasmanager_ventas'
--

--
-- Current Database: `gasmanager_clientes`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `gasmanager_clientes` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `gasmanager_clientes`;

--
-- Table structure for table `abonos_credito`
--

DROP TABLE IF EXISTS `abonos_credito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `abonos_credito` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `fecha_abono` date NOT NULL,
  `folio_abono` varchar(30) NOT NULL,
  `metodo_pago` varchar(20) DEFAULT NULL,
  `monto` decimal(12,2) NOT NULL,
  `notas` text,
  `referencia_pago` varchar(50) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `credito_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_5qn1kw87kh15he2iv7gmdrgnc` (`folio_abono`),
  KEY `FKhq4pbbp9ox79xn4c9fy4mwlb4` (`credito_id`),
  CONSTRAINT `FKhq4pbbp9ox79xn4c9fy4mwlb4` FOREIGN KEY (`credito_id`) REFERENCES `creditos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `abonos_credito`
--

LOCK TABLES `abonos_credito` WRITE;
/*!40000 ALTER TABLE `abonos_credito` DISABLE KEYS */;
/*!40000 ALTER TABLE `abonos_credito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `calle` varchar(100) DEFAULT NULL,
  `celular` varchar(15) DEFAULT NULL,
  `ciudad` varchar(100) DEFAULT NULL,
  `codigo_cliente` varchar(50) NOT NULL,
  `codigo_postal` varchar(10) DEFAULT NULL,
  `colonia` varchar(100) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `curp` varchar(18) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `estado` varchar(100) DEFAULT NULL,
  `nombre_comercial` varchar(255) DEFAULT NULL,
  `numero_exterior` varchar(20) DEFAULT NULL,
  `numero_interior` varchar(20) DEFAULT NULL,
  `razon_social` varchar(150) DEFAULT NULL,
  `rfc` varchar(13) DEFAULT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `tipo_persona` enum('FISICA','MORAL') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_8gduvo4bbw5hhyyivmrmdonau` (`codigo_cliente`),
  UNIQUE KEY `UK_put0r7uw2ww6awlkw8srv4ydb` (`rfc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creditos`
--

DROP TABLE IF EXISTS `creditos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creditos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `dia_pago` int DEFAULT NULL,
  `dias_mora` int DEFAULT NULL,
  `estado` enum('ACTIVO','CANCELADO','EN_COBRANZA','PAGADO','VENCIDO') NOT NULL,
  `fecha_inicio` date DEFAULT NULL,
  `fecha_ultimo_calculo_interes` date DEFAULT NULL,
  `fecha_ultimo_pago` date DEFAULT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `folio_credito` varchar(30) NOT NULL,
  `metodo_pago` enum('MENSUAL','PERSONALIZADO','QUINCENAL','SEMANAL') DEFAULT NULL,
  `monto_interes` decimal(12,2) DEFAULT NULL,
  `monto_interes_acumulado` decimal(12,2) DEFAULT NULL,
  `monto_mora_acumulado` decimal(12,2) DEFAULT NULL,
  `monto_pagado` decimal(12,2) DEFAULT NULL,
  `monto_total` decimal(12,2) NOT NULL,
  `notas` text,
  `plazo_meses` int DEFAULT NULL,
  `saldo_pendiente` decimal(12,2) DEFAULT NULL,
  `tasa_interes` decimal(5,2) DEFAULT NULL,
  `tasa_mora` decimal(5,2) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `cliente_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ojnyi3ywl3f9uhhe4y3bg9clg` (`folio_credito`),
  KEY `FKebytgljwj03rs91cbtjvc3cdk` (`cliente_id`),
  CONSTRAINT `FKebytgljwj03rs91cbtjvc3cdk` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creditos`
--

LOCK TABLES `creditos` WRITE;
/*!40000 ALTER TABLE `creditos` DISABLE KEYS */;
/*!40000 ALTER TABLE `creditos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'gasmanager_clientes'
--

--
-- Dumping routines for database 'gasmanager_clientes'
--

--
-- Current Database: `gasmanager_facturacion`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `gasmanager_facturacion` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `gasmanager_facturacion`;

--
-- Table structure for table `facturas`
--

DROP TABLE IF EXISTS `facturas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facturas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cadena_original` text,
  `cliente_codigo_postal` varchar(5) DEFAULT NULL,
  `cliente_email` varchar(100) DEFAULT NULL,
  `cliente_id` bigint DEFAULT NULL,
  `cliente_nombre` varchar(150) NOT NULL,
  `cliente_regimen_fiscal` varchar(3) DEFAULT NULL,
  `cliente_rfc` varchar(13) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `descuento` decimal(12,2) DEFAULT NULL,
  `estado` enum('CANCELADA','EMITIDA','ERROR_TIMBRADO','PENDIENTE_TIMBRADO') NOT NULL,
  `fecha_emision` datetime(6) NOT NULL,
  `fecha_timbrado` datetime(6) DEFAULT NULL,
  `folio` varchar(10) DEFAULT NULL,
  `folio_factura` varchar(30) NOT NULL,
  `forma_pago` enum('CHEQUE','EFECTIVO','TARJETA_CREDITO','TARJETA_DEBITO','TRANSFERENCIA') DEFAULT NULL,
  `iva` decimal(12,2) NOT NULL,
  `metodo_pago` enum('PAGO_EN_PARCIALIDADES','PAGO_EN_UNA_EXHIBICION') DEFAULT NULL,
  `no_certificado` varchar(20) DEFAULT NULL,
  `no_certificado_sat` varchar(20) DEFAULT NULL,
  `observaciones` text,
  `pdf_path` varchar(255) DEFAULT NULL,
  `sello_cfd` text,
  `sello_sat` text,
  `serie` varchar(10) DEFAULT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  `total` decimal(12,2) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `uuid_cfdi` varchar(36) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `xml_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_l74hh4ckshxawujsmfrkxa259` (`folio_factura`),
  UNIQUE KEY `UK_jnvmlpyiprsmi0tmiodwe824n` (`uuid_cfdi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facturas`
--

LOCK TABLES `facturas` WRITE;
/*!40000 ALTER TABLE `facturas` DISABLE KEYS */;
/*!40000 ALTER TABLE `facturas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facturas_detalle`
--

DROP TABLE IF EXISTS `facturas_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facturas_detalle` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cantidad` decimal(10,3) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `iva` decimal(12,2) DEFAULT NULL,
  `monto` decimal(12,2) NOT NULL,
  `precio_unitario` decimal(10,2) DEFAULT NULL,
  `producto_descripcion` varchar(200) DEFAULT NULL,
  `subtotal` decimal(12,2) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `venta_fecha` datetime(6) NOT NULL,
  `venta_folio` varchar(50) NOT NULL,
  `venta_id` bigint NOT NULL,
  `factura_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK47v5yic0ws5djxs49yu117x9w` (`factura_id`),
  CONSTRAINT `FK47v5yic0ws5djxs49yu117x9w` FOREIGN KEY (`factura_id`) REFERENCES `facturas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facturas_detalle`
--

LOCK TABLES `facturas_detalle` WRITE;
/*!40000 ALTER TABLE `facturas_detalle` DISABLE KEYS */;
/*!40000 ALTER TABLE `facturas_detalle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'gasmanager_facturacion'
--

--
-- Dumping routines for database 'gasmanager_facturacion'
--

--
-- Current Database: `gasmanager_nomina`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `gasmanager_nomina` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `gasmanager_nomina`;

--
-- Table structure for table `departamentos`
--

DROP TABLE IF EXISTS `departamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `departamentos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `departamentos`
--

LOCK TABLES `departamentos` WRITE;
/*!40000 ALTER TABLE `departamentos` DISABLE KEYS */;
INSERT INTO `departamentos` VALUES (1,_binary '','2026-07-26 06:12:06.595364','SISTEMA','Ventas ','Ventas','2026-07-26 06:12:06.595392','SISTEMA',0);
/*!40000 ALTER TABLE `departamentos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `empleados`
--

DROP TABLE IF EXISTS `empleados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `empleados` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `apellido_materno` varchar(50) DEFAULT NULL,
  `apellido_paterno` varchar(50) NOT NULL,
  `banco` varchar(50) DEFAULT NULL,
  `celular` varchar(15) DEFAULT NULL,
  `codigo_empleado` varchar(50) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `curp` varchar(18) DEFAULT NULL,
  `direccion` text,
  `email` varchar(100) DEFAULT NULL,
  `fecha_baja` date DEFAULT NULL,
  `fecha_ingreso` date NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `nombre` varchar(50) NOT NULL,
  `nss` varchar(20) DEFAULT NULL,
  `numero_cuenta` varchar(20) DEFAULT NULL,
  `rfc` varchar(13) DEFAULT NULL,
  `salario_diario` decimal(12,2) DEFAULT NULL,
  `salario_mensual` decimal(12,2) DEFAULT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `tipo_contrato` enum('INDEFINIDO','POR_TIEMPO_DETERMINADO','PRACTICAS','TEMPORAL') DEFAULT NULL,
  `tipo_jornada` enum('DIURNA','MIXTA','NOCTURNA','TURNO_ROTATIVO') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `departamento_id` bigint DEFAULT NULL,
  `puesto_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_424hpcm1dftn3bngbg7bwt8yv` (`codigo_empleado`),
  UNIQUE KEY `UK_9388qq89dhsl54fn29okch1mk` (`rfc`),
  KEY `FK1dvvcamb3oxb2d9xqd9taug0u` (`departamento_id`),
  KEY `FK3ywen1vm1hi0garl3dhnsalva` (`puesto_id`),
  CONSTRAINT `FK1dvvcamb3oxb2d9xqd9taug0u` FOREIGN KEY (`departamento_id`) REFERENCES `departamentos` (`id`),
  CONSTRAINT `FK3ywen1vm1hi0garl3dhnsalva` FOREIGN KEY (`puesto_id`) REFERENCES `puestos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empleados`
--

LOCK TABLES `empleados` WRITE;
/*!40000 ALTER TABLE `empleados` DISABLE KEYS */;
/*!40000 ALTER TABLE `empleados` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `empleados_puesto_historial`
--

DROP TABLE IF EXISTS `empleados_puesto_historial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `empleados_puesto_historial` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `fecha_fin` date DEFAULT NULL,
  `fecha_inicio` date NOT NULL,
  `motivo_cambio` varchar(200) DEFAULT NULL,
  `salario_diario` decimal(12,2) DEFAULT NULL,
  `salario_mensual` decimal(12,2) DEFAULT NULL,
  `empleado_id` bigint NOT NULL,
  `puesto_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmaw9an32d0jt5r4pcej136cle` (`empleado_id`),
  KEY `FKt0f03kr8wmhcwmwsf37x69264` (`puesto_id`),
  CONSTRAINT `FKmaw9an32d0jt5r4pcej136cle` FOREIGN KEY (`empleado_id`) REFERENCES `empleados` (`id`),
  CONSTRAINT `FKt0f03kr8wmhcwmwsf37x69264` FOREIGN KEY (`puesto_id`) REFERENCES `puestos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empleados_puesto_historial`
--

LOCK TABLES `empleados_puesto_historial` WRITE;
/*!40000 ALTER TABLE `empleados_puesto_historial` DISABLE KEYS */;
/*!40000 ALTER TABLE `empleados_puesto_historial` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `incidencias`
--

DROP TABLE IF EXISTS `incidencias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `incidencias` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `autorizado_por` varchar(100) DEFAULT NULL,
  `cantidad` decimal(10,2) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `fecha` date NOT NULL,
  `monto` decimal(12,2) DEFAULT NULL,
  `observaciones` text,
  `tipo` enum('AGUINALDO','BONO','FALTA','HORA_EXTRA_DOBLE','HORA_EXTRA_TRIPLE','PERMISO_CON_GOCE','PERMISO_SIN_GOCE','PRIMA_VACACIONAL','RETARDO','VACACION') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `empleado_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKivhx6id6simg6160gv3nla6ft` (`empleado_id`),
  CONSTRAINT `FKivhx6id6simg6160gv3nla6ft` FOREIGN KEY (`empleado_id`) REFERENCES `empleados` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `incidencias`
--

LOCK TABLES `incidencias` WRITE;
/*!40000 ALTER TABLE `incidencias` DISABLE KEYS */;
/*!40000 ALTER TABLE `incidencias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nominas`
--

DROP TABLE IF EXISTS `nominas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nominas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `estado` enum('CANCELADA','PAGADA','PROCESADA') NOT NULL,
  `fecha_pago` date DEFAULT NULL,
  `fecha_procesamiento` datetime(6) DEFAULT NULL,
  `folio_nomina` varchar(30) NOT NULL,
  `observaciones` text,
  `periodo_fin` date NOT NULL,
  `periodo_inicio` date NOT NULL,
  `total_bonos` decimal(14,2) DEFAULT NULL,
  `total_deducciones` decimal(14,2) DEFAULT NULL,
  `total_empleados` int DEFAULT NULL,
  `total_horas_extras` decimal(14,2) DEFAULT NULL,
  `total_impuestos` decimal(14,2) DEFAULT NULL,
  `total_neto` decimal(14,2) DEFAULT NULL,
  `total_sueldos` decimal(14,2) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_dqx895ix2wv3n51i4efm1fbeg` (`folio_nomina`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nominas`
--

LOCK TABLES `nominas` WRITE;
/*!40000 ALTER TABLE `nominas` DISABLE KEYS */;
/*!40000 ALTER TABLE `nominas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nominas_detalle`
--

DROP TABLE IF EXISTS `nominas_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nominas_detalle` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bonos` decimal(12,2) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `cuota_sindical` decimal(12,2) DEFAULT NULL,
  `dias_trabajados` decimal(10,2) DEFAULT NULL,
  `faltas` decimal(10,2) DEFAULT NULL,
  `faltas_descuento` decimal(12,2) DEFAULT NULL,
  `horas_extras` decimal(10,2) DEFAULT NULL,
  `horas_extras_monto` decimal(12,2) DEFAULT NULL,
  `infonavit` decimal(12,2) DEFAULT NULL,
  `isr` decimal(12,2) DEFAULT NULL,
  `neto_pagar` decimal(12,2) DEFAULT NULL,
  `otras_deducciones` decimal(12,2) DEFAULT NULL,
  `seguro_social` decimal(12,2) DEFAULT NULL,
  `sueldo_base` decimal(12,2) DEFAULT NULL,
  `total_deducciones` decimal(12,2) DEFAULT NULL,
  `total_gravado` decimal(12,2) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `empleado_id` bigint NOT NULL,
  `nomina_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2550ia0bw64hw2vktxibqf6wl` (`empleado_id`),
  KEY `FK6i7mfbyq6utdjr3tf6h36fqhn` (`nomina_id`),
  CONSTRAINT `FK2550ia0bw64hw2vktxibqf6wl` FOREIGN KEY (`empleado_id`) REFERENCES `empleados` (`id`),
  CONSTRAINT `FK6i7mfbyq6utdjr3tf6h36fqhn` FOREIGN KEY (`nomina_id`) REFERENCES `nominas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nominas_detalle`
--

LOCK TABLES `nominas_detalle` WRITE;
/*!40000 ALTER TABLE `nominas_detalle` DISABLE KEYS */;
/*!40000 ALTER TABLE `nominas_detalle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `puestos`
--

DROP TABLE IF EXISTS `puestos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `puestos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `riesgo_puesto` varchar(20) DEFAULT NULL,
  `salario_base` decimal(12,2) DEFAULT NULL,
  `salario_diario` decimal(12,2) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `puestos`
--

LOCK TABLES `puestos` WRITE;
/*!40000 ALTER TABLE `puestos` DISABLE KEYS */;
INSERT INTO `puestos` VALUES (1,_binary '','2026-07-26 06:11:43.361299','SISTEMA','Venta de combustible y aceites','Despachador','MEDIO',4500.00,150.00,'2026-07-26 06:11:43.362087','SISTEMA',0);
/*!40000 ALTER TABLE `puestos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'gasmanager_nomina'
--

--
-- Dumping routines for database 'gasmanager_nomina'
--

--
-- Current Database: `gasmanager_compras`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `gasmanager_compras` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `gasmanager_compras`;

--
-- Table structure for table `detalles_orden_compra`
--

DROP TABLE IF EXISTS `detalles_orden_compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalles_orden_compra` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cantidad` decimal(10,3) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `iva` decimal(12,2) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `producto_id` bigint NOT NULL,
  `producto_nombre` varchar(100) NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  `tipo_producto` enum('ACEITE_MOTOR','ADITIVO','COMBUSTIBLE_DIESEL','COMBUSTIBLE_MAGNA','COMBUSTIBLE_PREMIUM','OTRO') NOT NULL,
  `total` decimal(12,2) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `orden_compra_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5meqh3ntsthdona9ung826afc` (`orden_compra_id`),
  CONSTRAINT `FK5meqh3ntsthdona9ung826afc` FOREIGN KEY (`orden_compra_id`) REFERENCES `ordenes_compra` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalles_orden_compra`
--

LOCK TABLES `detalles_orden_compra` WRITE;
/*!40000 ALTER TABLE `detalles_orden_compra` DISABLE KEYS */;
/*!40000 ALTER TABLE `detalles_orden_compra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ordenes_compra`
--

DROP TABLE IF EXISTS `ordenes_compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ordenes_compra` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `estado` enum('CANCELADA','PARCIAL','PENDIENTE','RECIBIDA') NOT NULL,
  `factura` varchar(50) DEFAULT NULL,
  `fecha_entrega` date DEFAULT NULL,
  `fecha_orden` date NOT NULL,
  `folio_orden` varchar(30) NOT NULL,
  `iva` decimal(12,2) DEFAULT NULL,
  `observaciones` text,
  `subtotal` decimal(12,2) DEFAULT NULL,
  `total` decimal(12,2) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `proveedor_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_nn3ph2yqcver3lfm6j4dtcmyx` (`folio_orden`),
  KEY `FK7ximp03n72hmygxmikaapavac` (`proveedor_id`),
  CONSTRAINT `FK7ximp03n72hmygxmikaapavac` FOREIGN KEY (`proveedor_id`) REFERENCES `proveedores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ordenes_compra`
--

LOCK TABLES `ordenes_compra` WRITE;
/*!40000 ALTER TABLE `ordenes_compra` DISABLE KEYS */;
/*!40000 ALTER TABLE `ordenes_compra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proveedores`
--

DROP TABLE IF EXISTS `proveedores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedores` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `codigo_proveedor` varchar(50) NOT NULL,
  `contacto` varchar(100) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `direccion` text,
  `email` varchar(100) DEFAULT NULL,
  `nombre` varchar(150) NOT NULL,
  `rfc` varchar(13) DEFAULT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_p1slk3xonh8v4hgmc72esnfn1` (`codigo_proveedor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proveedores`
--

LOCK TABLES `proveedores` WRITE;
/*!40000 ALTER TABLE `proveedores` DISABLE KEYS */;
/*!40000 ALTER TABLE `proveedores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'gasmanager_compras'
--

--
-- Dumping routines for database 'gasmanager_compras'
--

--
-- Current Database: `gasmanager_inventarios`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `gasmanager_inventarios` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `gasmanager_inventarios`;

--
-- Table structure for table `aceites`
--

DROP TABLE IF EXISTS `aceites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aceites` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `codigo` varchar(100) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `marca` varchar(50) DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `precio_compra` decimal(10,2) NOT NULL,
  `precio_venta` decimal(10,2) NOT NULL,
  `presentacion` varchar(20) DEFAULT NULL,
  `stock_actual` int NOT NULL,
  `stock_maximo` int NOT NULL,
  `stock_minimo` int NOT NULL,
  `tipo_aceite` varchar(30) DEFAULT NULL,
  `ubicacion` varchar(50) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ny00yqmlmg8u3o1xm2t9wjdsd` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aceites`
--

LOCK TABLES `aceites` WRITE;
/*!40000 ALTER TABLE `aceites` DISABLE KEYS */;
/*!40000 ALTER TABLE `aceites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aceites_bodega`
--

DROP TABLE IF EXISTS `aceites_bodega`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aceites_bodega` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `aceite_id` bigint NOT NULL,
  `activo` bit(1) DEFAULT NULL,
  `codigo` varchar(50) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `precio_compra` decimal(10,2) DEFAULT NULL,
  `precio_venta` decimal(10,2) DEFAULT NULL,
  `proveedor` varchar(100) DEFAULT NULL,
  `stock_actual` int NOT NULL,
  `stock_maximo` int NOT NULL,
  `stock_minimo` int NOT NULL,
  `ubicacion` varchar(50) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aceites_bodega`
--

LOCK TABLES `aceites_bodega` WRITE;
/*!40000 ALTER TABLE `aceites_bodega` DISABLE KEYS */;
/*!40000 ALTER TABLE `aceites_bodega` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aceites_dispensario`
--

DROP TABLE IF EXISTS `aceites_dispensario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aceites_dispensario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `aceite_id` bigint NOT NULL,
  `activo` bit(1) DEFAULT NULL,
  `codigo` varchar(50) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `dispensario_id` bigint NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `precio_venta` decimal(10,2) DEFAULT NULL,
  `stock_actual` int NOT NULL,
  `stock_maximo` int NOT NULL,
  `stock_minimo` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aceites_dispensario`
--

LOCK TABLES `aceites_dispensario` WRITE;
/*!40000 ALTER TABLE `aceites_dispensario` DISABLE KEYS */;
/*!40000 ALTER TABLE `aceites_dispensario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cargas_pipa`
--

DROP TABLE IF EXISTS `cargas_pipa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cargas_pipa` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cargado_por` varchar(50) DEFAULT NULL,
  `cargado_por_id` bigint DEFAULT NULL,
  `costo_total` decimal(12,2) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `factura` varchar(50) DEFAULT NULL,
  `fecha_carga` datetime(6) NOT NULL,
  `folio` varchar(50) NOT NULL,
  `observaciones` varchar(500) DEFAULT NULL,
  `precio_compra` decimal(10,2) DEFAULT NULL,
  `proveedor` varchar(100) DEFAULT NULL,
  `tipo_combustible` enum('DIESEL','GASOLINA_MAGNA','GASOLINA_PREMIUM','MAGNA','PREMIUM') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `volumen` decimal(10,3) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_tf73g5nbt4g9dea135nkqslty` (`folio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cargas_pipa`
--

LOCK TABLES `cargas_pipa` WRITE;
/*!40000 ALTER TABLE `cargas_pipa` DISABLE KEYS */;
/*!40000 ALTER TABLE `cargas_pipa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `combustibles`
--

DROP TABLE IF EXISTS `combustibles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `combustibles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `descripcion` varchar(200) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `precio_actual` decimal(10,2) NOT NULL,
  `tipo` enum('DIESEL','GASOLINA_MAGNA','GASOLINA_PREMIUM','MAGNA','PREMIUM') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hlup26e00p222e4uikdmrcume` (`tipo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `combustibles`
--

LOCK TABLES `combustibles` WRITE;
/*!40000 ALTER TABLE `combustibles` DISABLE KEYS */;
/*!40000 ALTER TABLE `combustibles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `compras_aceites`
--

DROP TABLE IF EXISTS `compras_aceites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `compras_aceites` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `aceite_id` bigint NOT NULL,
  `aceite_nombre` varchar(100) NOT NULL,
  `cantidad` int NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `factura` varchar(50) DEFAULT NULL,
  `fecha_compra` datetime(6) NOT NULL,
  `folio` varchar(50) NOT NULL,
  `iva` decimal(12,2) DEFAULT NULL,
  `observaciones` text,
  `precio_unitario` decimal(10,2) DEFAULT NULL,
  `proveedor` varchar(100) DEFAULT NULL,
  `realizado_por_id` bigint DEFAULT NULL,
  `realizado_por_nombre` varchar(100) DEFAULT NULL,
  `subtotal` decimal(12,2) DEFAULT NULL,
  `total` decimal(12,2) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_sr756mp1gqg3yvumned5u51dr` (`folio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `compras_aceites`
--

LOCK TABLES `compras_aceites` WRITE;
/*!40000 ALTER TABLE `compras_aceites` DISABLE KEYS */;
/*!40000 ALTER TABLE `compras_aceites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventario_combustible`
--

DROP TABLE IF EXISTS `inventario_combustible`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario_combustible` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `capacidad_tanque` decimal(10,3) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `nombre` varchar(50) NOT NULL,
  `stock_actual` decimal(10,3) NOT NULL,
  `stock_minimo` decimal(10,3) DEFAULT NULL,
  `tipo_combustible` enum('DIESEL','GASOLINA_MAGNA','GASOLINA_PREMIUM','MAGNA','PREMIUM') NOT NULL,
  `ultima_lectura` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_j6bepjtn6w9020yo97u5m204f` (`tipo_combustible`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventario_combustible`
--

LOCK TABLES `inventario_combustible` WRITE;
/*!40000 ALTER TABLE `inventario_combustible` DISABLE KEYS */;
INSERT INTO `inventario_combustible` VALUES (1,_binary '',50000.000,'2026-07-25 00:28:26.183106',NULL,'Gasolina Magna',24991.666,5000.000,'MAGNA','2026-07-25 12:51:12.788179','2026-07-25 12:51:12.911172','sistema',2),(2,_binary '',50000.000,'2026-07-25 00:28:27.874411',NULL,'Gasolina Premium',25000.000,5000.000,'PREMIUM',NULL,'2026-07-25 00:28:27.874416',NULL,0),(3,_binary '',50000.000,'2026-07-25 00:28:27.968580',NULL,'Diesel',25000.000,5000.000,'DIESEL',NULL,'2026-07-25 00:28:27.968585',NULL,0);
/*!40000 ALTER TABLE `inventario_combustible` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `precios_historicos`
--

DROP TABLE IF EXISTS `precios_historicos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `precios_historicos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cambiado_por` varchar(50) DEFAULT NULL,
  `cambiado_por_id` bigint DEFAULT NULL,
  `fecha_cambio` datetime(6) NOT NULL,
  `motivo_cambio` varchar(200) DEFAULT NULL,
  `precio_anterior` decimal(10,2) DEFAULT NULL,
  `precio_nuevo` decimal(10,2) NOT NULL,
  `combustible_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3y64acicrj43ge0nnlxgifkth` (`combustible_id`),
  CONSTRAINT `FK3y64acicrj43ge0nnlxgifkth` FOREIGN KEY (`combustible_id`) REFERENCES `combustibles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `precios_historicos`
--

LOCK TABLES `precios_historicos` WRITE;
/*!40000 ALTER TABLE `precios_historicos` DISABLE KEYS */;
/*!40000 ALTER TABLE `precios_historicos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transferencias_aceites`
--

DROP TABLE IF EXISTS `transferencias_aceites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transferencias_aceites` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `aceite_id` bigint NOT NULL,
  `aceite_nombre` varchar(100) NOT NULL,
  `cantidad` int NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `dispensario_destino_id` bigint NOT NULL,
  `dispensario_origen_id` bigint DEFAULT NULL,
  `fecha_movimiento` datetime(6) NOT NULL,
  `folio` varchar(50) NOT NULL,
  `motivo` varchar(200) DEFAULT NULL,
  `observaciones` text,
  `realizado_por_id` bigint DEFAULT NULL,
  `realizado_por_nombre` varchar(100) DEFAULT NULL,
  `tipo` varchar(30) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_56pfxm9utmaqn9vo9kh8sshsp` (`folio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transferencias_aceites`
--

LOCK TABLES `transferencias_aceites` WRITE;
/*!40000 ALTER TABLE `transferencias_aceites` DISABLE KEYS */;
/*!40000 ALTER TABLE `transferencias_aceites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'gasmanager_inventarios'
--

--
-- Dumping routines for database 'gasmanager_inventarios'
--

--
-- Current Database: `gasmanager_lealtad`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `gasmanager_lealtad` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `gasmanager_lealtad`;

--
-- Table structure for table `canje_recompensa`
--

DROP TABLE IF EXISTS `canje_recompensa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `canje_recompensa` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `estado` enum('APROBADO','CANCELADO','PENDIENTE') DEFAULT NULL,
  `fecha_canje` datetime(6) DEFAULT NULL,
  `puntos_usados` int NOT NULL,
  `venta_id` bigint DEFAULT NULL,
  `recompensa_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKifkyq9oiuvne6rd7frqe3rq4q` (`recompensa_id`),
  CONSTRAINT `FKifkyq9oiuvne6rd7frqe3rq4q` FOREIGN KEY (`recompensa_id`) REFERENCES `recompensas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `canje_recompensa`
--

LOCK TABLES `canje_recompensa` WRITE;
/*!40000 ALTER TABLE `canje_recompensa` DISABLE KEYS */;
/*!40000 ALTER TABLE `canje_recompensa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cuenta_puntos`
--

DROP TABLE IF EXISTS `cuenta_puntos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuenta_puntos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `saldo_puntos` int NOT NULL,
  `venta_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cuenta_puntos`
--

LOCK TABLES `cuenta_puntos` WRITE;
/*!40000 ALTER TABLE `cuenta_puntos` DISABLE KEYS */;
/*!40000 ALTER TABLE `cuenta_puntos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `programa_lealtad`
--

DROP TABLE IF EXISTS `programa_lealtad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `programa_lealtad` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `factor_puntos` int NOT NULL,
  `fin` datetime(6) DEFAULT NULL,
  `inicio` datetime(6) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `programa_lealtad`
--

LOCK TABLES `programa_lealtad` WRITE;
/*!40000 ALTER TABLE `programa_lealtad` DISABLE KEYS */;
/*!40000 ALTER TABLE `programa_lealtad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recompensas`
--

DROP TABLE IF EXISTS `recompensas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recompensas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `costo_puntos` int NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `tipo` enum('DESCUENTO','EFECTIVO','PRODUCTO_GRATIS') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recompensas`
--

LOCK TABLES `recompensas` WRITE;
/*!40000 ALTER TABLE `recompensas` DISABLE KEYS */;
/*!40000 ALTER TABLE `recompensas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaccion`
--

DROP TABLE IF EXISTS `transaccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaccion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fecha` datetime(6) DEFAULT NULL,
  `litros` double NOT NULL,
  `monto` double NOT NULL,
  `puntos_generados` int NOT NULL,
  `venta_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaccion`
--

LOCK TABLES `transaccion` WRITE;
/*!40000 ALTER TABLE `transaccion` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaccion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'gasmanager_lealtad'
--

--
-- Dumping routines for database 'gasmanager_lealtad'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-03 15:57:21
