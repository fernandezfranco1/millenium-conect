-- Agregar columna disciplina a la tabla alumnos
ALTER TABLE alumnos ADD COLUMN disciplina VARCHAR(20) DEFAULT 'Taekwondo';

-- Actualizar alumnos existentes con disciplina Taekwondo
UPDATE alumnos SET disciplina = 'Taekwondo' WHERE disciplina IS NULL;

-- Hacer la columna categoria nullable ya que solo aplica para Taekwondo
ALTER TABLE alumnos MODIFY COLUMN categoria VARCHAR(50) NULL;
