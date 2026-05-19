package com.aduanas.comex.clasificacionms.service;


import com.aduanas.comex.clasificacionms.dto.ClasificacionResponseDTO;
import com.aduanas.comex.clasificacionms.dto.EvaluarClasificacionRequestDTO;
import com.aduanas.comex.clasificacionms.entity.Clasificacion;
import com.aduanas.comex.clasificacionms.enums.TipoClasificacion;
import com.aduanas.comex.clasificacionms.repository.ClasificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClasificacionService {

    private final ClasificacionRepository clasificacionRepository;

    public ClasificacionResponseDTO evaluar(EvaluarClasificacionRequestDTO dto) {

        // Regla simple para la demo
        boolean permitido = true;
        TipoClasificacion tipo = TipoClasificacion.IMPORTACION;
        BigDecimal impuesto = dto.getValorDeclarado()
                .multiply(new BigDecimal("0.06"));
        String observaciones = "Mercancía permitida";

        Clasificacion clasificacion = Clasificacion.builder()
                .cargaId(dto.getCargaId())
                .tipoClasificacion(tipo)
                .permitido(permitido)
                .montoImpuesto(impuesto)
                .observaciones(observaciones)
                .fechaEvaluacion(LocalDateTime.now())
                .build();

        clasificacion = clasificacionRepository.save(clasificacion);

        return mapToResponse(clasificacion);
    }

    public List<ClasificacionResponseDTO> listar() {
        return clasificacionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ClasificacionResponseDTO obtenerPorId(Long id) {
        Clasificacion clasificacion = clasificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clasificación no encontrada"));

        return mapToResponse(clasificacion);
    }

    private ClasificacionResponseDTO mapToResponse(Clasificacion clasificacion) {
        return ClasificacionResponseDTO.builder()
                .id(clasificacion.getId())
                .cargaId(clasificacion.getCargaId())
                .tipoClasificacion(clasificacion.getTipoClasificacion().name())
                .permitido(clasificacion.getPermitido())
                .montoImpuesto(clasificacion.getMontoImpuesto())
                .observaciones(clasificacion.getObservaciones())
                .build();
    }
}