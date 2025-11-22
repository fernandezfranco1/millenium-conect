-- Agregar columna id_clase a la tabla asistencias
ALTER TABLE asistencias 
ADD COLUMN id_clase BIGINT;

-- Agregar foreign key constraint
ALTER TABLE asistencias
ADD CONSTRAINT fk_asistencia_clase 
FOREIGN KEY (id_clase) REFERENCES clases(id_clase);

-- Si ya tienes datos, necesitarás asignar una clase por defecto
-- UPDATE asistencias SET id_clase = (SELECT MIN(id_clase) FROM clases) WHERE id_clase IS NULL;

-- Después de asignar valores, hacer la columna NOT NULL
-- ALTER TABLE asistencias MODIFY id_clase BIGINT NOT NULL;
