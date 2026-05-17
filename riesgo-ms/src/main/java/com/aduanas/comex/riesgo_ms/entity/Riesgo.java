package com.aduanas.comex.riesgo_ms.entity;

// IMPORTAR ENUMS
import com.aduanas.comex.riesgo_ms.enums.EstadoRiesgo;
import com.aduanas.comex.riesgo_ms.enums.NivelRiesgo;

// IMPORTACIONES JPA
import jakarta.persistence.*;

// LOMBOK
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// FECHA
import java.time.LocalDateTime;

// @Data
// genera:
// getters
// setters
// toString
// equals
@Data
// Marca esta clase como tabla SQL
@Entity
// Constructor vacío
@NoArgsConstructor
// Constructor completo
@AllArgsConstructor

// Nombre tabla MySQL
@Table(name = "riesgo")
public class Riesgo {

        // ===============================
        // PRIMARY KEY
        // ===============================
        @Id
        // ID autoincremental
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // ===============================
        // DESCRIPCIÓN RIESGO
        // ===============================
        // nullable = false
        // NO permite null
        @Column(nullable = false)
        private String descripcion;

        // ===============================
        // ENUM NIVEL RIESGO
        // ===============================
        // Guarda enum como texto:
        // ALTO
        // BAJO
        // MEDIO
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private NivelRiesgo nivel;

        // ===============================
        // ENUM ESTADO RIESGO
        // ===============================
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private EstadoRiesgo estado;

        // ===============================
        // TIPO CARGA
        // ===============================
        @Column(nullable = false)
        private String tipoCarga;

        // ===============================
        // PAÍS ORIGEN
        // ===============================
        @Column(nullable = false)
        private String origen;

        // ===============================
        // FECHA REGISTRO
        // ===============================
        @Column(nullable = false)
        private LocalDateTime fechaRegistro;
}
