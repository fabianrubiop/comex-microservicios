package com.aduanas.com.documentosms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "documentos")


public class Documento {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_documento")
    private Long documentoId;

    @Column(name = "tipo_documento", nullable = false)
    private String tipoDocumento;

    @Column(name = "ruta_archivo",nullable = false, length = 100)
    private String rutaArchivo;

    // DATOS EXTRAÍDOS:
    // * En un sistema sin OCR, este campo actúa como una "Nota de Auditoría".
    // * Aquí guardas un resumen de lo que el analista validó manualmente o
    // * detalles que no tienen un campo propio (ej: "Factura revisada, coincide con el sello").
    // */
    @Column(name = "datos_extraidos",nullable = false, length = 100)
    private String datosExtraidos;

    //Guarda una respuesta la constante que se asigne.
    //En Java, un Enum es técnicamente un tipo especial de clase. La diferencia es que,
    // mientras una clase normal puede tener infinitas instancias (muchos documentos),
    // el Enum solo tiene las instancias que tú escribas (solo existe un PENDIENTE, un APROBADO y un DENEGADO).
    //El Enum es el "Molde": Tú defines qué palabras son válidas para ese atributo.
    //El Atributo es la "Caja": Guardas una de esas palabras en la base de datos.

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_validacion")
    private EstadoValidacion estadoValidacion; // Estado del flujo: PENDIENTE, APROBADO o RECHAZADO.

    // Fecha y hora exacta en la que se registró el documento.

    @Column(name = "fecha_documentos" ,nullable = false)
    private LocalDate fechaDocumento;

}




