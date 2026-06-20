CREATE TABLE notificaciones (
    id_notificacion BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_carga BIGINT NOT NULL,
    mensaje VARCHAR(255) NOT NULL,
    tipo_notificacion VARCHAR(30) NOT NULL,
    destinatario VARCHAR(100) NOT NULL,
    estado_notificacion VARCHAR(30) NOT NULL,
    fecha_notificacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);