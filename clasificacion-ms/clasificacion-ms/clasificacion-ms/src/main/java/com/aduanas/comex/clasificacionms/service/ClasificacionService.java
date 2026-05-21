package com.aduanas.comex.clasificacionms.service;


import com.aduanas.comex.clasificacionms.client.CargaClient;
import com.aduanas.comex.clasificacionms.client.DocumentoClient;
import com.aduanas.comex.clasificacionms.client.NotificacionClient;
import com.aduanas.comex.clasificacionms.client.PagoClient;
import com.aduanas.comex.clasificacionms.dto.ClasificacionResponseDTO;
import com.aduanas.comex.clasificacionms.dto.EvaluarClasificacionRequestDTO;
import com.aduanas.comex.clasificacionms.entity.Clasificacion;
import com.aduanas.comex.clasificacionms.enums.TipoClasificacion;
import com.aduanas.comex.clasificacionms.repository.ClasificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClasificacionService {

    // 1. Repositorio Local (Para guardar en la BD de clasificación)
    private final ClasificacionRepository clasificacionRepository;

    // 2. Clientes Feign (Los puentes síncronos HTTP hacia los otros microservicios)
    private final CargaClient cargaClient;
    private final DocumentoClient documentoClient;
    private final PagoClient pagoClient;
    private final NotificacionClient notificacionClient;

    @Transactional(rollbackFor = Exception.class) // Usamos el de Spring para asegurar la transacción
    public Clasificacion clasificarMercaderia(Long cargaId, BigDecimal valorDeclarado, String observaciones, TipoClasificacion tipo, String emailUsuario) {

        // =========================================================================
        // PASO 1: Lógica de Negocio Local (Calcular el 19% de IVA Chileno)
        // =========================================================================
        BigDecimal impuestoCalculado = valorDeclarado.multiply(new BigDecimal("0.19"));

        // Guardar el registro en la tabla 'clasificaciones' de Laragon
        Clasificacion clasificacion = new Clasificacion();
        clasificacion.setCargaId(cargaId);
        clasificacion.setMontoImpuesto(impuestoCalculado);
        clasificacion.setObservaciones(observaciones);
        clasificacion.setTipo(tipo); // ¡Aquí ya no marcará rojo si @Data está en la entidad!

        Clasificacion guardada = clasificacionRepository.save(clasificacion);

        // =========================================================================
        // PASO 2: Comunicación Síncrona (Feign) -> Tienen que completarse sí o sí
        // =========================================================================

        // Le avisa a Cargas que guarde el impuesto y cambie a "CLASIFICADA"
        cargaClient.actualizarImpuestoYEstado(cargaId, impuestoCalculado, "CLASIFICADA");

        // Le avisa a Documentos que genere la Declaración de Ingreso (DIN)
        documentoClient.generarDeclaracionIngreso(cargaId, impuestoCalculado, "12345678-9");

        // Le avisa a Pagos que genere la orden de cobro financiera
        pagoClient.crearOrdenDePago(cargaId, impuestoCalculado);

        // =========================================================================
        // PASO 3: Comunicación Asíncrona (Liberación de hilo en Notificaciones)
        // =========================================================================
        // Se dispara la petición HTTP por Feign, pero el controlador de destino
        // responde un 200 OK de inmediato liberando este flujo, mientras manda el mail en segundo plano.
        notificacionClient.enviarNotificacionClasificacion(
                emailUsuario,
                "Tu carga N° " + cargaId + " ha sido clasificada. Total impuesto a pagar: $" + impuestoCalculado
        );

        // Retornamos la clasificación guardada localmente
        return guardada;
    }

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

    public ClasificacionResponseDTO obtenerPorCargaId(Long cargaId) {
        Clasificacion clasificacion = clasificacionRepository.findByCargaId(cargaId)
                .orElseThrow(() -> new RuntimeException("Clasificación no encontrada para la carga: " + cargaId));

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