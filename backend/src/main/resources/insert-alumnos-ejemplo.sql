-- Script para insertar 15 alumnos de ejemplo
-- Ejecutar en DBeaver después de que las tablas estén creadas

INSERT INTO alumnos (nombre, apellido, dni, telefono, peso, categoria, genero, fecha_nacimiento) VALUES
('Juan', 'Pérez', '12345678', '1123456789', 65.5, 'Azul', 'Masculino', '2010-05-15'),
('María', 'González', '23456789', '1134567890', 52.3, 'Verde punta azul', 'Femenino', '2012-08-22'),
('Carlos', 'Rodríguez', '34567890', '1145678901', 70.2, 'Rojo', 'Masculino', '2008-03-10'),
('Ana', 'Martínez', '45678901', '1156789012', 48.7, 'Amarillo punta verde', 'Femenino', '2013-11-30'),
('Luis', 'López', '56789012', '1167890123', 75.8, 'Negro primer dan', 'Masculino', '2005-07-18'),
('Sofía', 'Fernández', '67890123', '1178901234', 55.4, 'Verde', 'Femenino', '2011-02-25'),
('Diego', 'García', '78901234', '1189012345', 68.9, 'Azul punta roja', 'Masculino', '2009-09-14'),
('Valentina', 'Sánchez', '89012345', '1190123456', 50.1, 'Blanco punta amarilla', 'Femenino', '2014-06-08'),
('Mateo', 'Ramírez', '90123456', '1101234567', 72.6, 'Rojo punta negra', 'Masculino', '2007-12-03'),
('Lucía', 'Torres', '01234567', '1112345678', 53.2, 'Amarillo', 'Femenino', '2012-04-20'),
('Santiago', 'Flores', '11223344', '1123456780', 77.5, 'Negro segundo dan', 'Masculino', '2004-01-15'),
('Camila', 'Díaz', '22334455', '1134567891', 49.8, 'Verde punta azul', 'Femenino', '2011-10-12'),
('Nicolás', 'Morales', '33445566', '1145678902', 69.3, 'Azul', 'Masculino', '2009-07-28'),
('Isabella', 'Romero', '44556677', '1156789013', 51.6, 'Blanco', 'Femenino', '2015-03-05'),
('Thiago', 'Álvarez', '55667788', '1167890124', 80.2, 'Negra', 'Masculino', '2006-11-22');
