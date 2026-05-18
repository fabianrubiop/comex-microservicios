package com.aduanas.com.pagosms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargaExternaDto {
    private Long id; // <-- Tu Postman muestra "id", no "cargaId"
    private BigDecimal valorDeclarado; // <-- Mismo nombre que la foto
    private BigDecimal peso; // <-- Mismo nombre que la foto
    private String estado; // <-- Aquí caerá el "CLASIFICADA"
}