-- Crear la base de datos db_unit
CREATE DATABASE IF NOT EXISTS dbunit;

-- Usar la base de datos db_unit
USE dbunit;

-- Crear la tabla usuarios
CREATE TABLE usuarios (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) DEFAULT NULL,
    email VARCHAR(30) DEFAULT NULL,
    password VARCHAR(16) DEFAULT NULL,
    isLogged TINYINT(1) DEFAULT NULL
);
