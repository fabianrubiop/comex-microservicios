package com.aduanas.comex.clasificacionms.entity;

import com.aduanas.comex.clasificacionms.enums.TipoClasificacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "clasificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clasificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clasificacionId;

    @Column(name = "carga_id", nullable = false)
    private Long cargaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoClasificacion tipoClasificacion;

    @Column(nullable = false)
    private Boolean permitido;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montoImpuesto;

    @Column(length = 500)
    private String observaciones;

    @Column(name = "fecha_evaluacion", nullable = false)
    private LocalDateTime fechaEvaluacion;
}
