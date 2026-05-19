package com.aduanas.comex.cargams.service;


import com.aduanas.comex.cargams.client.ClasificacionClient;
import com.aduanas.comex.cargams.dto.CargaResponseDTO;
import com.aduanas.comex.cargams.dto.CrearCargaRequestDTO;
import com.aduanas.comex.cargams.dto.external.ClasificacionRequestDTO;
import com.aduanas.comex.cargams.dto.external.ClasificacionResponseDTO;
import com.aduanas.comex.cargams.entity.Carga;
import com.aduanas.comex.cargams.enums.EstadoCarga;
import com.aduanas.comex.cargams.repository.CargaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CargaService {

    private final CargaRepository cargaRepository;
    private final ClasificacionClient clasificacionClient;

    @Transactional
    public CargaResponseDTO crear(CrearCargaRequestDTO dto) {
        log.info("Iniciando registro de nueva carga: {}", dto.getNumeroDeclaracion());

        // 1. Guardar la entidad en nuestra propia base de datos local
        Carga carga = Carga.builder()
                .nroDeclaracion(dto.getNumeroDeclaracion())
                .descripcion(dto.getDescripcion())
                .paisOrigen(dto.getPaisOrigen())
                .valorDeclarado(dto.getValorDeclarado())
                .peso(dto.getPeso())
                .importadorRut(dto.getImportadorRut())
                .estado(EstadoCarga.REGISTRADA)
                .fechaCreacion(LocalDateTime.now())
                .build();

        carga = cargaRepository.save(carga);
        log.debug("Carga persistida con éxito de manera local con ID: {}", carga.getId());

        // 2. Preparar la petición para el Microservicio de Clasificación
        ClasificacionRequestDTO extRequest = ClasificacionRequestDTO.builder()
                .cargaId(carga.getId())
                .descripcionMercancia(carga.getDescripcion())
                .paisOrigen(carga.getPaisOrigen())
                .valorDeclarado(carga.getValorDeclarado())
                .build();

        log.info("Llamando de forma síncrona a clasificacion-cumplimiento-ms para Carga ID: {}", carga.getId());

        // 3. Comunicación Inter-servicio vía Feign
        ClasificacionResponseDTO extResponse = clasificacionClient.evaluar(extRequest);
        log.info("Respuesta recibida desde Clasificación. Permitido: {}", extResponse.getPermitido());

        // 4. Actualizar el estado de la carga de acuerdo a la respuesta del otro MS
        if (Boolean.TRUE.equals(extResponse.getPermitido())) {
            carga.setEstado(EstadoCarga.CLASIFICADA);
        } else {
            carga.setEstado(EstadoCarga.RECHAZADA);
        }
        cargaRepository.save(carga);

        // 5. Construir y retornar el DTO unificado final
        return CargaResponseDTO.builder()
                .id(carga.getId())
                .numeroDeclaracion(carga.getNroDeclaracion())
                .descripcion(carga.getDescripcion())
                .paisOrigen(carga.getPaisOrigen())
                .valorDeclarado(carga.getValorDeclarado())
                .importadorRut(carga.getImportadorRut())
                .estado(carga.getEstado().name())
                .fechaCreacion(carga.getFechaCreacion())
                .permitido(extResponse.getPermitido())
                .montoImpuesto(extResponse.getMontoImpuesto())
                .observaciones(extResponse.getObservaciones())
                .build();
    }

    public List<Carga> listar() {
        return cargaRepository.findAll();
    }

    public Carga obtenerPorId(Long id) {
        return cargaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carga no encontrada con ID: " + id));
    }

    public void actualizarImpuestoYEstado(Long id, BigDecimal impuesto, com.aduanas.comex.cargams.enums.EstadoCarga nuevoEstado) {
        // 1. Buscar la carga en tu base de datos. Si no existe, lanza un error.
        Carga carga = cargaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la carga con ID: " + id));

        // 2. Modificar los valores con los setters correspondientes de tu entidad Carga
        carga.setMontoImpuesto(impuesto);
        carga.setEstado(nuevoEstado); // ¡Ahora sí compilará porque ambos son EstadoCarga!

        // 3. Guardar los cambios en MySQL
        cargaRepository.save(carga);
    }
}
