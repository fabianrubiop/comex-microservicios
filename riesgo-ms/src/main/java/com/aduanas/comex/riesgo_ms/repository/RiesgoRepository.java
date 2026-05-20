package com.aduanas.comex.riesgo_ms.repository;

import com.aduanas.comex.riesgo_ms.entity.Riesgo;
import com.aduanas.comex.riesgo_ms.enums.CanalRiesgo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {


    List<Riesgo> findByCanalAsignado(CanalRiesgo canal);

    List<Riesgo> findByCargaId(Long cargaId);
}