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
     * 1. Sub-metodo auxiliar con @Transactional.
     * Guarda la carga inicial en la base de datos y hace COMMIT de inmediato al terminar.
     */
    @Transactional
    public Carga guardarCargaInicial(CrearCargaRequestDTO dto) {
        Carga carga = Carga.builder()
                .numeroDeclaracion(dto.getNumeroDeclaracion())
                .descripcion(dto.getDescripcion())
                .paisOrigen(dto.getPaisOrigen())
                .valorDeclarado(dto.getValorDeclarado())
                .peso(dto.getPeso())
                .importadorRut(dto.getImportadorRut())
                .estado(EstadoCarga.REGISTRADA) // 1️⃣ Nace en REGISTRADA
                .fechaCreacion(LocalDateTime.now())
                .build();

        return cargaRepository.save(carga);
    }

    /**
     * 2. Método principal del servicio.
     * INTACTO: Mantiene la lógica síncrona original y la comunicación Feign.
     */
    public CargaResponseDTO crear(CrearCargaRequestDTO dto) {
        log.info("Iniciando registro de nueva carga: {}", dto.getNumeroDeclaracion());

        // PASO A: Persistir localmente
        Carga carga = guardarCargaInicial(dto);
        log.debug("Carga persistida localmente con ID: {}", carga.getIdCarga());

        // PASO B: Mapear datos para Clasificación
        ClasificacionRequestDTO extRequest = ClasificacionRequestDTO.builder()
                .idCarga(carga.getIdCarga())
                .descripcionMercancia(carga.getDescripcion())
                .paisOrigen(carga.getPaisOrigen())
                .valorDeclarado(carga.getValorDeclarado())
                .importadorRut(carga.getImportadorRut())
                .build();

        // PASO C: Comunicación Feign
        log.info("Llamando a clasificacion-ms para Carga ID: {}", carga.getIdCarga());
        ClasificacionResponseDTO extResponse = clasificacionClient.evaluar(carga.getIdCarga(), extRequest);
        log.info("Respuesta recibida. Permitido: {}", extResponse.getPermitido());

        // PASO D: Actualizar el estado en memoria usando la respuesta externa
        // No dependemos de una re-lectura de BD, usamos el objeto 'carga' que ya existe
        EstadoCarga estadoFinal = Boolean.TRUE.equals(extResponse.getPermitido())
                ? EstadoCarga.CLASIFICADA
                : EstadoCarga.RECHAZADA;

        carga.setEstado(estadoFinal);

        // Si fue rechazada, guardamos explícitamente en BD el cambio de estado
        if (EstadoCarga.RECHAZADA.equals(estadoFinal)) {
            actualizarEstadoRechazado(carga.getIdCarga());
        }

        // PASO E: Construir y retornar el DTO unificado
        // Pasamos 'carga' actualizada y los datos de la respuesta externa
        return convertirADto(carga, extResponse.getPermitido(), extResponse.getObservaciones());
    }

    /**
     * Sub-método auxiliar para aislar transaccionalmente la actualización por rechazo
     */
    @Transactional
    public void actualizarEstadoRechazado(Long idCarga) {
        cargaRepository.findById(idCarga).ifPresent(c -> {
            c.setEstado(EstadoCarga.RECHAZADA);
            cargaRepository.save(c);
        });
    }

    /**
     * 3. LISTAR CARGAS: Corregido para mapear dinámicamente según el Enum.
     */
    public List<CargaResponseDTO> listar() {
        return cargaRepository.findAll().stream()
                .map(carga -> convertirADto(carga, null, "Consulta general"))
                .toList();
    }

    /**
     * 4. OBTENER DETALLE POR ID: Corregido para reflejar el estado real en cada GET.
     */
    public CargaResponseDTO obtenerPorId(Long idCarga) {
        Carga carga = obtenerEntidadPorId(idCarga);
        return convertirADto(carga, null, "Consulta individual");
    }

    /**
     * Método interno para consumo dentro de la clase.
     */
    private Carga obtenerEntidadPorId(Long idCarga) {
        return cargaRepository.findById(idCarga)
                .orElseThrow(() -> new RuntimeException("Carga no encontrada con ID: " + idCarga));
    }

    /**
     * ASIGNAR IMPUESTO DESDE EL CONTROLADOR (Invocado remotamente por Clasificación).
     */
    @Transactional
    public void actualizarImpuestoYEstado(Long idCarga, BigDecimal impuesto, EstadoCarga nuevoEstado, String voucher) {
        Carga carga = cargaRepository.findById(idCarga)
                .orElseThrow(() -> new RuntimeException("No se encontró la carga con ID: " + idCarga));

        carga.setMontoImpuesto(impuesto);
        carga.setEstado(nuevoEstado);
        carga.setIdTransaccionExterna(voucher);
        cargaRepository.save(carga);
    }

    /**
     * 🔄 CONVERTIDOR MAESTRO: Determina el campo 'permitido' según el Enum real de la base de datos.
     */
    private CargaResponseDTO convertirADto(Carga carga, Boolean permitidoForzado, String observacionesAdicionales) {

        Boolean permitidoFinal = permitidoForzado;

        // Si no viene un valor forzado desde el método crear(), evaluamos según el escalón del Enum en la BD
        if (permitidoFinal == null) {
            if (carga.getEstado() == EstadoCarga.RECHAZADA) {
                permitidoFinal = false;
            } else if (carga.getEstado() == EstadoCarga.CLASIFICADA || carga.getEstado() == EstadoCarga.LIBERADA) {
                // 2️⃣ 5️⃣ En estos estados la aduana ya dio el visto bueno formal
                permitidoFinal = true;
            } else {
                // 1️⃣ 3️⃣ 4️⃣ Para REGISTRADA, EN_INSPECCION o PENDIENTE_PAGO es falso/espera hasta que cambie de fase
                permitidoFinal = false;
            }
        }

        return CargaResponseDTO.builder()
                .idCarga(carga.getIdCarga())
                .numeroDeclaracion(carga.getNumeroDeclaracion())
                .descripcion(carga.getDescripcion())
                .paisOrigen(carga.getPaisOrigen())
                .valorDeclarado(carga.getValorDeclarado())
                .peso(carga.getPeso())
                .importadorRut(carga.getImportadorRut())
                .estado(carga.getEstado().name()) // Muestra dinámicamente: REGISTRADA, CLASIFICADA, PENDIENTE_PAGO, etc.
                .fechaCreacion(carga.getFechaCreacion())
                .permitido(permitidoFinal) // Refleja la verdad del Enum
                .montoImpuesto(carga.getMontoImpuesto() != null ? carga.getMontoImpuesto() : BigDecimal.ZERO)
                .idTransaccionExterna(carga.getIdTransaccionExterna())
                .observaciones(observacionesAdicionales + " - Fase actual: " + carga.getEstado().name())
                .build();
    }

    @Transactional
    public void eliminarCarga(Long idCarga) {
        // 1. Validamos si existe
        Carga carga = cargaRepository.findById(idCarga)
                .orElseThrow(() -> new RuntimeException("La carga no existe"));

        // 2. Lógica de Negocio: No se puede eliminar algo que ya fue LIBERADO
        if (carga.getEstado() == EstadoCarga.LIBERADA) {
            throw new RuntimeException("No se puede eliminar una carga que ya fue liberada y nacionalizada.");
        }

        // 3. Borrado físico
        cargaRepository.deleteById(idCarga);
    }

    public CargaResponseDTO findByNumeroDeclaracion(String numeroDeclaracion) {
        Carga carga = cargaRepository.findByNumeroDeclaracion(numeroDeclaracion)
                .orElseThrow(() -> new RuntimeException("Carga no encontrada con el número de declaración: " + numeroDeclaracion));

        return convertirADto(carga, null, "Consulta por declaración aduanera");
    }
}