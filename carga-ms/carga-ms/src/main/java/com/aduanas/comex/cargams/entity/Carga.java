package com.aduanas.comex.cargams.entity;

import com.aduanas.comex.cargams.enums.EstadoCarga;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cargas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cargaId;

    @Column(name = "numero_declaracion", nullable = false, unique = true, length = 50)
    private String nroDeclaracion;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "pais_origen", nullable = false, length = 100)
    private String paisOrigen;

    @Column(name = "valor_declarado", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorDeclarado;

    @Column(name = "rut_importador", nullable = false, length = 12)
    private String importadorRut;

    @Column(name = "estado", nullable = false, length = 30)
    private EstadoCarga estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

}
