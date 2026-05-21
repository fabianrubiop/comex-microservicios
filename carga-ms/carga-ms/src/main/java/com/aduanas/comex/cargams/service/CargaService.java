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

    /**
     * 1. Sub-método auxiliar con @Transactional.
     * Guarda la carga inicial en la base de datos y hace COMMIT de inmediato al terminar,
     * liberando los bloqueos de tablas antes de que hagamos la llamada por red (Feign).
     */
    @Transactional
    public Carga guardarCargaInicial(CrearCargaRequestDTO dto) {
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

        return cargaRepository.save(carga);
    }

    /**
     * 2. Método principal del servicio.
     * NOTA: Aquí quitamos el @Transactional para que la comunicación inter-servicio
     * vía Feign ocurra FUERA de un contexto transaccional bloqueante.
     */
    public CargaResponseDTO crear(CrearCargaRequestDTO dto) {
        log.info("Iniciando registro de nueva carga: {}", dto.getNumeroDeclaracion());

        // PASO A: Persistir localmente y liberar la transacción inmediatamente
        Carga carga = guardarCargaInicial(dto);
        log.debug("Carga persistida con éxito de manera local con ID: {}", carga.getId());

        // PASO B: Mapear datos reales para el microservicio externo de clasificación
        ClasificacionRequestDTO extRequest = ClasificacionRequestDTO.builder()
                .cargaId(carga.getId())
                .descripcionMercancia(carga.getDescripcion())
                .paisOrigen(carga.getPaisOrigen())
                .valorDeclarado(carga.getValorDeclarado())
                .build();

        log.info("Llamando de forma síncrona a clasificacion-cumplimiento-ms para Carga ID: {}", carga.getId());

        // PASO C: Comunicación Feign segura.
        // Clasificación podrá invocar a 'actualizarImpuestoYEstado' sin sufrir bloqueos en la BD (Deadlock).
        ClasificacionResponseDTO extResponse = clasificacionClient.evaluar(carga.getId(), extRequest);
        log.info("Respuesta recibida desde Clasificación. Permitido: {}", extResponse.getPermitido());

        // PASO D: Volver a leer la entidad fresca desde la BD (ya modificada por Clasificación con sus impuestos)
        carga = obtenerPorId(carga.getId());

        // Si fue rechazada externamente, actualizamos el estado de manera aislada
        if (Boolean.FALSE.equals(extResponse.getPermitido())) {
            actualizarEstadoRechazado(carga.getId());
            carga.setEstado(EstadoCarga.RECHAZADA);
        }

        // PASO E: Construir y retornar el DTO unificado final para Postman
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

    /**
     * Sub-método auxiliar para aislar transaccionalmente la actualización por rechazo
     */
    @Transactional
    public void actualizarEstadoRechazado(Long id) {
        cargaRepository.findById(id).ifPresent(c -> {
            c.setEstado(EstadoCarga.RECHAZADA);
            cargaRepository.save(c);
        });
    }

    public List<Carga> listar() {
        return cargaRepository.findAll();
    }

    public Carga obtenerPorId(Long id) {
        return cargaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carga no encontrada con ID: " + id));
    }

    @Transactional
    public void actualizarImpuestoYEstado(Long id, BigDecimal impuesto, EstadoCarga nuevoEstado) {
        Carga carga = cargaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la carga con ID: " + id));

        carga.setMontoImpuesto(impuesto);
        carga.setEstado(nuevoEstado);

        cargaRepository.save(carga);
    }
}