--Agrega columna peso a tabla cargas para permitir comunicación entre cargas y pagos por Feign
ALTER TABLE cargas ADD COLUMN peso DECIMAL(10,2) NOT NULL DEFAULT 0.00;