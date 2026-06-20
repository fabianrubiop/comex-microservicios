CREATE TABLE documentos (
    id_documento BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_carga BIGINT NOT NULL,
    tipo_documento VARCHAR(100) NOT NULL,
    ruta_archivo VARCHAR(255) NOT NULL,
    estado_validacion VARCHAR(50) NOT NULL,
    fecha_documento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);