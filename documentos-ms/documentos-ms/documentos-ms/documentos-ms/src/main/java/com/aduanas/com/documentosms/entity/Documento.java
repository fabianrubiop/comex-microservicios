package com.aduanas.com.documentosms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "documento")


public class Documento {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Long documentoId;

    @Column(name = "tipo_documento", nullable = false)
    private String tipoDocumento;

    @Column(name = "ruta_archivo", nullable = false, length = 255) // Más espacio por si acaso
    private String rutaArchivo;

    // CORREGIDO: Cambiamos el nombre del atributo para que calce con el Service
    // Le quitamos el nullable = false para que el cliente pueda subirlo sin que el analista haya escrito aún.
    // Aumentamos el largo a 500 para que quepa todo el texto del analista.
    @Column(name = "observacion_manual", length = 500)
    private String observacionManual;

    private String ResultadoRevision;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_validacion", nullable = false)
    private EstadoValidacion estadoValidacion;

    @Column(name = "fecha_documentos", nullable = false)
    private LocalDateTime fechaDocumento;
}






