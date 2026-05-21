package com.aduanas.comex.riesgo_ms.exception;

// SPRING
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// MAPA JSON
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ======================================================
    // MANEJAR RiesgoException
    // ======================================================
    //
    // Cuando ocurra:
    // throw new RiesgoException(...)
    //
    // Spring ejecutará automáticamente
    // este método.
    //
    @ExceptionHandler(RiesgoException.class)
    public ResponseEntity<Map<String, String>> manejarRiesgoException(

            RiesgoException ex
    ) {

        // ==========================================
        // CREAR RESPUESTA JSON
        // ==========================================
        Map<String, String> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());

        // ==========================================
        // RETORNAR ERROR 404
        // ==========================================
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // ======================================================
    // MANEJAR ERRORES GENERALES
    // ======================================================
    //
    // Captura cualquier otro error.
    //
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarException(
            Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("mensaje", "Error interno del servidor");

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}