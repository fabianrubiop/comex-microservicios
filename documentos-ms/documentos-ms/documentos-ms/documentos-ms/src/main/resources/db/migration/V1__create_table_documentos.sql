CREATE TABLE pagos (
    pago_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    carga_id BIGINT NOT NULL, -- Relación lógica con el microservicio de Cargas
    monto DECIMAL(10,2) NOT NULL, -- BigDecimal con precisión absoluta para las lucas
    moneda VARCHAR(3) NOT NULL, -- Código ISO (ej: 'CLP', 'USD')
    estado_pago VARCHAR(30) NOT NULL, -- Guardará 'PENDIENTE', 'PROCESANDO', 'COMPLETADO' o 'FALLIDO'
    transaccion_externa_id VARCHAR(100) UNIQUE, -- El váucher manual de Postman (ÚNICO para que no se duplique)
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);