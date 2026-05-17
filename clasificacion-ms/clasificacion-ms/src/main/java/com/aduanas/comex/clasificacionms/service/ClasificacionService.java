package com.aduanas.comex.clasificacionms.service;

import com.aduanas.comex.clasificacionms.dto.ClasificacionRequestDTO;
import com.aduanas.comex.clasificacionms.dto.ClasificacionResponseDTO;

import java.util.List;

public interface ClasificacionService {
    ClasificacionResponseDTO evaluar(ClasificacionRequestDTO dto);
    List<ClasificacionResponseDTO> listar();
    ClasificacionResponseDTO obtenerPorId(Long clasificacionId);
}
