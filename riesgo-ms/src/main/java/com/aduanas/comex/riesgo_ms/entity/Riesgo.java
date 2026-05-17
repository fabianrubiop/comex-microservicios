package com.aduanas.comex.riesgo_ms.entity;

import com.aduanas.comex.riesgo_ms.enums.EstadoRiesgo;
import com.aduanas.comex.riesgo_ms.enums.NivelRiesgo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "riesgo")

public class Riesgo {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_riesgo")
        private Long id;

        @Column(nullable = false, length = 150)
        private String descripcion;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private NivelRiesgo nivel;

        @Column(nullable = false, length = 100)
        private String tipoMercancia;

        @Column(nullable = false, length = 100)
        private String paisOrigen;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private EstadoRiesgo estado;

        @Column(nullable = false)
        private LocalDateTime fechaRegistro;



}
