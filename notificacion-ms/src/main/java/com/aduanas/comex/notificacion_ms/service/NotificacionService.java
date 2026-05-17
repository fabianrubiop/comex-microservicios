package com.aduanas.comex.notificacion_ms.service;

import com.aduanas.comex.notificacion_ms.dto.NotificacionRequestDTO;
import com.aduanas.comex.notificacion_ms.dto.NotificacionResponseDTO;
import com.aduanas.comex.notificacion_ms.entity.Notificacion;
import com.aduanas.comex.notificacion_ms.enums.EstadoNotificacion;
import com.aduanas.comex.notificacion_ms.enums.TipoNotificacion;
import com.aduanas.comex.notificacion_ms.repository.NotificacionRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository repository;

    public NotificacionService(NotificacionRepository repository) {
        this.repository = repository;
    }

    // CREAR NOTIFICACIÓN
    public NotificacionResponseDTO crear(
            NotificacionRequestDTO dto
    ) {

        Notificacion notificacion =
                new Notificacion();

        notificacion.setMensaje(
                dto.getMensaje()
        );

        notificacion.setDestinatario(
                dto.getDestinatario()
        );

        notificacion.setTipo(
                TipoNotificacion.valueOf(dto.getTipo())
        );

        notificacion.setEstado(
                EstadoNotificacion.valueOf(dto.getEstado())
        );

        notificacion.setFecha(
                LocalDateTime.now()
        );

        Notificacion guardada =
                repository.save(notificacion);

        return convertirResponseDTO(guardada);
    }

    // LISTAR TODAS LAS NOTIFICACIONES
    public List<NotificacionResponseDTO> listar() {

        return repository.findAll()
                .stream()
                .map(this::convertirResponseDTO)
                .toList();
    }

    // BUSCAR NOTIFICACIÓN POR ID
    public NotificacionResponseDTO buscarPorId(
            Long id
    ) {

        Notificacion entity =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notificación no encontrada"
                                ));

        return convertirResponseDTO(entity);
    }

    // BUSCAR POR ESTADO
    public List<NotificacionResponseDTO>
    buscarPorEstado(String estado) {

        List<Notificacion> lista =
                repository.findByestado(
                        EstadoNotificacion.valueOf(estado)
                );

        return lista.stream()
                .map(this::convertirResponseDTO)
                .toList();
    }

    // ACTUALIZAR NOTIFICACIÓN
    public NotificacionResponseDTO actualizar(
            Long id,
            NotificacionRequestDTO dto
    ) {

        Notificacion entity =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notificación no encontrada"
                                ));

        entity.setMensaje(
                dto.getMensaje()
        );

        entity.setDestinatario(
                dto.getDestinatario()
        );

        entity.setTipo(
                TipoNotificacion.valueOf(dto.getTipo())
        );

        entity.setEstado(
                EstadoNotificacion.valueOf(dto.getEstado())
        );

        Notificacion actualizada =
                repository.save(entity);

        return convertirResponseDTO(actualizada);
    }

    // ELIMINAR NOTIFICACIÓN
    public void eliminar(Long id) {

        if (!repository.existsById(id)) {

            throw new RuntimeException(
                    "Notificación no encontrada"
            );
        }

        repository.deleteById(id);
    }

    // CONVERTIR ENTITY -> RESPONSE DTO
    private NotificacionResponseDTO convertirResponseDTO(
            Notificacion entity
    ) {

        return NotificacionResponseDTO.builder()
                .id(entity.getId())
                .mensaje(entity.getMensaje())
                .destinatario(entity.getDestinatario())
                .tipo(entity.getTipo().name())
                .estado(entity.getEstado().name())
                .fecha(entity.getFecha())
                .build();
    }
}
