package com.aduanas.com.pagosms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificacionBancoDto {

    @NotNull(message = "El ID del pago es obligatorio")
    private Long pagoId;

    @NotNull(message = "El ID de la carga es obligatorio")
    private Long cargaId;

    @NotBlank(message = "El código de váucher no puede estar vacío")
    private String transaccionExternalId; // El que escribes a mano en Postman

    @NotNull(message = "El estado de la transacción es obligatorio")
    private Boolean transaccionExitosa; // true o false
}
