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

        // 2. Mapeamos los datos reales para el microservicio de clasificación
        ClasificacionRequestDTO extRequest = ClasificacionRequestDTO.builder()
                .cargaId(carga.getId())
                .descripcionMercancia(carga.getDescripcion())
                .paisOrigen(carga.getPaisOrigen())
                .valorDeclarado(carga.getValorDeclarado())
                .build();

        log.info("Llamando de forma síncrona a clasificacion-cumplimiento-ms para Carga ID: {}", carga.getId());

        // 3. Comunicación Inter-servicio vía Feign
        ClasificacionResponseDTO extResponse = clasificacionClient.evaluar(carga.getId(), extRequest);
        log.info("Respuesta recibida desde Clasificación. Permitido: {}", extResponse.getPermitido());

        // 4. Dependencia real: Si fue rechazada, lo marcamos. Si fue permitida, recargamos la entidad
        // para obtener el "PENDIENTE_PAGO" e impuestos reales que el otro MS inyectó en nuestra BD.
        if (Boolean.FALSE.equals(extResponse.getPermitido())) {
            carga.setEstado(EstadoCarga.RECHAZADA);
            cargaRepository.save(carga);
        } else {
            // Recargamos el registro fresco de la BD (con Arancel + IVA ya calculados)
            carga = cargaRepository.findById(carga.getId()).orElse(carga);
        }

        // 5. Construir y retornar el DTO unificado final para Postman
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
                .montoImpuesto(carga.getMontoImpuesto() != null ? carga.getMontoImpuesto() : BigDecimal.ZERO)
                .observaciones(extResponse.getObservaciones() != null ? extResponse.getObservaciones() : "Sin observaciones")
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
        Carga carga = cargaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la carga con ID: " + id));

        carga.setMontoImpuesto(impuesto);
        carga.setEstado(nuevoEstado);

        cargaRepository.save(carga);
    }
}