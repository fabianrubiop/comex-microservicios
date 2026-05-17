package com.aduanas.com.pagosms.dto;

import com.aduanas.com.pagosms.entity.EstadoPago;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data

public class PagoResponseDTO {
    private Long pagoId;
    private Long cargaId;
    private BigDecimal monto;
    private String moneda;
    private EstadoPago estadoPago;
    private String transaccionExternaId;
    private LocalDateTime fechaPago;
}
