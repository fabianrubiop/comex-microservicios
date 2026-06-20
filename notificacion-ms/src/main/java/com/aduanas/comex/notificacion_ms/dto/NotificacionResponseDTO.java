package com.aduanas.comex.notificacion_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionResponseDTO extends RepresentationModel<NotificacionResponseDTO> {

    // ✅ Sincronizado con el nombre de la entidad
    private Long idNotificacion;

    // ✅ SOLUCIONADO: Cambiado de cargaId a idCarga
    private Long idCarga;

    private String mensaje;

    private String tipo;

    private String destinatario;

    private String estado;

    // ✅ Sincronizado con el nombre de la entidad
    private LocalDateTime fechaNotificacion;
}