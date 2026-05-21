package com.aduanas.com.documentosms.entity;

import com.aduanas.com.documentosms.Enum.EstadoValidacion;
import com.aduanas.com.documentosms.Enum.EstadoValidacionArchivo; // <-- Importamos tu otro Enum
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "documento")
@Builder
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Long documentoId;

    @Column(name = "carga_id", nullable = false)
    private Long cargaId; // <-- ¡AHORA SÍ CONECTAMOS CON EL RESTO DEL MUNDO!

    @Column(name = "tipo_documento", nullable = false)
    private String tipoDocumento;

    @Column(name = "ruta_archivo", nullable = false, length = 255)
    private String rutaArchivo;

    @Column(name = "observacion_manual", length = 500)
    private String observacionManual;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado_revision")
    private EstadoValidacionArchivo resultadoRevision; // <-- Corregido el nombre y tipo a tu Enum real

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_validacion", nullable = false)
    private EstadoValidacion estadoValidacion;

    @Column(name = "fecha_documentos", nullable = false)
    private LocalDateTime fechaDocumento;
}