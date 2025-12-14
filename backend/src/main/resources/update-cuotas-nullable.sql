-- Modificar la tabla cuotas para permitir valores NULL en campos opcionales cuando el estado es Pendiente

ALTER TABLE cuotas 
    MODIFY COLUMN monto DECIMAL(10,2) NULL,
    MODIFY COLUMN fecha_pago DATE NULL,
    MODIFY COLUMN forma_pago VARCHAR(50) NULL;
