package com.aduanas.com.pagosms.dto;

import com.aduanas.com.pagosms.Enum.EstadoPago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data

public class PagoResponseDto {

    private Long pagoId;
    private Long cargaId;
    private String tramoAplicado;
    private String cargoAdministrativo;
    private BigDecimal impuestoIva;
    private BigDecimal totalAPagar;
    private String moneda;
    private EstadoPago estadoPago;
    private String transaccionExternaId;
    private LocalDateTime fechaPago;
}