package com.aduanas.com.documentosms.dto;

import com.aduanas.com.documentosms.entity.EstadoValidacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor


public class DocumentoRequestDTO {




    @NotBlank(message = "La ruta del archivo es obligatoria")
    private String rutaArchivo; //

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @NotBlank(message  = "La observaciones no pueden estar vacias")
    private String datosExtraidos;

    @NotNull(message = "Debe ingresar un estado válido.")
    private EstadoValidacion estadoValidacion;

    @NotNull(message = "La fecha del documento es obligatoria.")
    private LocalDateTime fechaDocumento;













}