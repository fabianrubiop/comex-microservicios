
CREATE TABLE evaluaciones_riesgo (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
carga_id BIGINT NOT NULL,
puntaje_riesgo INT NOT NULL,
canal_asignado VARCHAR(20) NOT NULL,
motivo_alerta TEXT,
evaluado_por VARCHAR(100) DEFAULT 'SISTEMA_AUTOMATICO',
fecha_evaluacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

CONSTRAINT uq_carga_riesgo UNIQUE (carga_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_riesgo_carga_id ON evaluaciones_riesgo(carga_id);