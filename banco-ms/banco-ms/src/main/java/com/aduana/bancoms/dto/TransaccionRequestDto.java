package com.aduana.bancoms.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data

public class TransaccionRequestDto {

     // <-- Te genera getters, setters y toString automáticamente

        private Long pagoId;       // El ID del pago que generó tu pagos-ms
        private Long cargaId;      // El ID de la carga asociada
        private BigDecimal monto;  // Cuánta plata se va a cobrar
        private String numeroTarjeta;
        private String nombreTitular;


}
