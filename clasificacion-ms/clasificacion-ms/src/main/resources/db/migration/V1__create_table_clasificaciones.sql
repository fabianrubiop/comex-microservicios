CREATE TABLE clasificaciones(
clasificacion_id BIGINT PRIMARY KEY AUTO_INCREMENT,
carga_id BIGINT NOT NULL,
tipo_clasificacion VARCHAR(50) NOT NULL,
permitido BOOLEAN NOT NULL,
monto_impuesto DECIMAL(15,2) NOT NULL,
observaciones VARCHAR(500),
fecha_evaluacion TIMESTAMP NOT NULL);