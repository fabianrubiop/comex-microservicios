CREATE TABLE cargas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    numero_declaracion VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL,
    pais_origen VARCHAR(100) NOT NULL,
    valor_declarado DECIMAL(15,2) NOT NULL,
    importador_rut VARCHAR(20) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL
);