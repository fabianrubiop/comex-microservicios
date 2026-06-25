package com.aduanas.com.pagosms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponseDto extends RepresentationModel<PagoResponseDto> {
    private Long idPago;
    private Long idCarga;
    private String tramo;
    private String cargoFijo;
    private BigDecimal montoImpuesto;
    private BigDecimal montoTotal;
    private String moneda;
    private String estadoPago;
    private String idTransaccionExterna;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaPago;
    private String resultadoTransaccion;
    private String voucherBancario;
}