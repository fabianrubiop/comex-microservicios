package com.aduanas.com.pagosms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NotBlank
@AllArgsConstructor
@NoArgsConstructor
@Data

public class PagoRequestDTO {

    @NotNull(message = "La cargaId es obligatorio")
    private Long cargaId;

    @NotNull
    @Positive(message = "El monto debe ser mayor a cero")
    //En temas de dinero, siempre es bueno asegurar que el monto no sea cero o un número negativo.
    // Es una capa extra de seguridad para que nadie te "hackee" el sistema enviando un pago de -100 dólares.
    private BigDecimal monto;

    @NotBlank(message = "Debe ingresar código de divisa ")
    private String moneda;

    private String transaccionExternaId;
    // El estado no suele pedirse, se asigna como PENDIENTE por defecto en el Service
}

