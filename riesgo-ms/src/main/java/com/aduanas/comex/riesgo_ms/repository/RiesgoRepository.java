package com.aduanas.comex.riesgo_ms.repository;

// ENTITY
import com.aduanas.comex.riesgo_ms.entity.Riesgo;

// ENUM
import com.aduanas.comex.riesgo_ms.enums.NivelRiesgo;

// JPA
import org.springframework.data.jpa.repository.JpaRepository;

// LIST
import java.util.List;

// JpaRepository genera CRUD automático
public interface RiesgoRepository  extends JpaRepository<Riesgo, Long> {

    // ===============================
    // QUERY METHOD
    // ===============================
    // Spring genera SQL automático
    //
    // SELECT * FROM riesgos
    // WHERE nivel = ?
    List<Riesgo>
    findByNivel(
            NivelRiesgo nivel
    );
}