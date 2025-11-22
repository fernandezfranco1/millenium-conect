-- Script para crear usuario administrador por defecto
-- Ejecutar después de que Spring Boot haya creado las tablas automáticamente

-- Usuario: admin
-- Contraseña: admin123
-- La contraseña está encriptada con BCrypt ($2a$10$...)

INSERT INTO usuarios (username, password, rol) 
VALUES ('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQNkhf0jXkKOPqXLGVp4G', 'ADMIN');

-- Nota: Si ya existe el usuario, primero eliminarlo con:
-- DELETE FROM usuarios WHERE username = 'admin';
