-- Crear tabla clases
CREATE TABLE IF NOT EXISTS clases (
    id_clase BIGINT AUTO_INCREMENT PRIMARY KEY,
    instructor VARCHAR(100) NOT NULL,
    apellido_instructor VARCHAR(100) NOT NULL,
    horario TIME NOT NULL,
    tipo VARCHAR(50) NOT NULL
);

-- Crear tabla intermedia clase_alumno para la relación ManyToMany
CREATE TABLE IF NOT EXISTS clase_alumno (
    id_clase BIGINT NOT NULL,
    id_alumno BIGINT NOT NULL,
    PRIMARY KEY (id_clase, id_alumno),
    FOREIGN KEY (id_clase) REFERENCES clases(id_clase) ON DELETE CASCADE,
    FOREIGN KEY (id_alumno) REFERENCES alumnos(id_alumno) ON DELETE CASCADE
);

-- Datos de ejemplo
INSERT INTO clases (instructor, apellido_instructor, horario, tipo) VALUES
('Juan', 'Pérez', '18:00:00', 'Taekwondo'),
('María', 'González', '19:30:00', 'Taekwondo'),
('Carlos', 'Rodríguez', '17:00:00', 'Taekwondo');
