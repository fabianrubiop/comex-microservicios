package com.aduanas.com.documentosms.repository;

import com.aduanas.com.documentosms.entity.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;





public interface DocumentoRepository  extends JpaRepository <Documento, Long>{}


