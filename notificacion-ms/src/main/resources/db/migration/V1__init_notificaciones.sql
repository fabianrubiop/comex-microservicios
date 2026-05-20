-- Crear tabla para el historial de alertas y notificaciones enviadas
CREATE TABLE notificaciones (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
carga_id BIGINT NOT NULL,                 -- Relación lógica con la carga asociada
destinatario VARCHAR(150) NOT NULL,       -- Correo electrónico del usuario o agente
tipo_canal VARCHAR(30) DEFAULT 'EMAIL',   -- Canal de despacho (EMAIL, SMS, etc.)
asunto VARCHAR(200) NOT NULL,             -- Título del mensaje
mensaje TEXT NOT NULL,                    -- Cuerpo de la notificación
estado_envio VARCHAR(30) NOT NULL,        -- Guardará estados como: 'PENDIENTE', 'ENVIADO', 'FALLIDO'
fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Restricción de control para evitar filas con correos vacíos
                                CONSTRAINT chk_destinatario_notif CHECK (destinatario LIKE '%@%')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índice para buscar rápidamente todas las notificaciones enviadas a una carga específica
CREATE INDEX idx_notificaciones_carga_id ON notificaciones(carga_id);