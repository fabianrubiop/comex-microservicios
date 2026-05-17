// =======================================================
// ================= REQUEST DTO =========================
// =======================================================

package com.aduanas.comex.riesgo_ms.dto;

// VALIDACIONES
import jakarta.validation.constraints.NotBlank;

// LOMBOK
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO entrada
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiesgoRequestDTO {

    // ===============================
    // VALIDACIONES
    // ===============================

    @NotBlank(
            message = " La descripción es obligatoria")
    private String descripcion;

    @NotBlank(
            message = "El nivel es obligatorio")
    private String nivel;

    @NotBlank(
            message = "El estado es obligatorio")
    private String estado;

    @NotBlank(
            message = "El tipo de carga es obligatorio")
    private String tipoCarga;

    @NotBlank(
            message = "El origen es obligatorio")
    private String origen;
}