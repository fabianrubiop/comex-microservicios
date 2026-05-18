package com.aduanas.comex.cargams.repository;

import com.aduanas.comex.cargams.entity.Carga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargaRepository extends JpaRepository<Carga, Long> {
}