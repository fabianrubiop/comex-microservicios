package com.aduanas.comex.riesgo_ms.entity;

import com.aduanas.comex.riesgo_ms.enums.CanalRiesgo;
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
        private Long id;

        @Column(nullable = false)
        private String descripcion;

        @Column(nullable = false)
        private String tipoCarga;


        @Column(nullable = false)
        private String origen;


        @Column(nullable = false)
        private LocalDateTime fechaRegistro;

        private Long cargaId;

        private Integer puntajeRiesgo;

        // Con esto indicamos que se guardará el texto del Enum en la BD
        @Enumerated(EnumType.STRING)
        private CanalRiesgo canalAsignado;

        private String motivoAlerta;

        private LocalDateTime fechaEvaluacion = LocalDateTime.now();

}
