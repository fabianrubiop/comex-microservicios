package com.aduanas.com.pagosms.repository;

import com.aduanas.com.pagosms.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {}
