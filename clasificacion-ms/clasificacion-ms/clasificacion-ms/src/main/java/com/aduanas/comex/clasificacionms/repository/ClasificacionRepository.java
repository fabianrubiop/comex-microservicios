package com.aduanas.comex.clasificacionms.repository;

import com.aduanas.comex.clasificacionms.entity.Clasificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClasificacionRepository extends JpaRepository<Clasificacion, Long> {
}
