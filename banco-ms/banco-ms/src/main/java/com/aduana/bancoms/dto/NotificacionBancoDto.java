package com.aduana.bancoms.dto;

import lombok.Data;

@Data

public class NotificacionBancoDto {
    private Long pagoId;
    private Long cargaId;
    private String transaccionExternalId; // Aquí va el número de váucher único
    private Boolean transaccionExitosa;   // true = aprobada, false = rechazada
}



