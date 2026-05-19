package com.aduanas.comex.riesgo_ms.exception;

// ======================================================
// ===================== IMPORTS =========================
// ======================================================

// HTTP STATUS
import org.springframework.http.HttpStatus;

// RESPUESTAS HTTP
import org.springframework.http.ResponseEntity;

// EXCEPCIONES VALIDACIONES DTO
import org.springframework.web.bind.MethodArgumentNotValidException;

// ANOTACIONES
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// MAPAS
import java.util.HashMap;
import java.util.Map;

// ======================================================
// ================= GLOBAL HANDLER ======================
// ======================================================
//
// Captura errores globalmente.
//
// Evita errores feos de Spring.
//
// Permite personalizar respuestas.
//
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ======================================================
    // ============ VALIDACIONES DTO ========================
    // ======================================================
    //
    // Captura errores como:
    //
    // @NotBlank
    // @Size
    // @Email
    //
    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )

    public ResponseEntity<
            Map<String, String>
            >
    manejarValidaciones(

            MethodArgumentNotValidException ex
    ) {

        // ======================================================
        // MAPA ERRORES
        // ======================================================
        //
        // Guardará:
        //
        // campo → mensaje
        //
        Map<String, String> errores =
                new HashMap<>();

        // ======================================================
        // RECORRER ERRORES
        // ======================================================
        ex.getBindingResult()

                .getFieldErrors()

                .forEach(error ->

                        errores.put(
                                // nombre campo
                                error.getField(),
                                // mensaje validación
                                error.getDefaultMessage()
                        )
                );

        // ======================================================
        // RESPONSE 400
        // ======================================================
        return new ResponseEntity<>(

                errores,

                HttpStatus.BAD_REQUEST
        );
    }

    // ======================================================
    // ============ EXCEPCIÓN PERSONALIZADA =================
    // ======================================================
    //
    // Captura:
    //
    // RiesgoException
    //
    @ExceptionHandler(
            RiesgoException.class
    )

    public ResponseEntity<String>
    manejarRiesgoException(

            RiesgoException ex
    ) {

        return new ResponseEntity<>(

                // mensaje error
                ex.getMessage(),

                // HTTP 400
                HttpStatus.BAD_REQUEST);
    }

    // ======================================================
    // ================= ERROR GENERAL ======================
    // ======================================================
    //
    // Captura cualquier error inesperado.
    //
    @ExceptionHandler(Exception.class)

    public ResponseEntity<String> manejarGeneral(Exception ex) {

        return new ResponseEntity<>(
                "Error interno del servidor",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}