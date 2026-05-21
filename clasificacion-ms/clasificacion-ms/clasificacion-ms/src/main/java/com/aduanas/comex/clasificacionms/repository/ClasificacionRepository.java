package com.aduanas.comex.clasificacionms.repository;

import com.aduanas.comex.clasificacionms.entity.Clasificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClasificacionRepository extends JpaRepository<Clasificacion, Long> {
    Optional<Clasificacion> findByCargaId(Long cargaId);
}
