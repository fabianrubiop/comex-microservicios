package com.aduanas.comex.notificacion_ms.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ======================================================
    // ERROR PERSONALIZADO
    // ======================================================
    //
    // Maneja:
    // throw new NotificacionException(...)
    //
    @ExceptionHandler(NotificacionException.class)
    public ResponseEntity<Map<String, String>> manejarNotificacionException(NotificacionException ex) {

        // ==========================================
        // CREAR JSON ERROR
        // ==========================================
        Map<String, String> error =
                new HashMap<>();
        // ==========================================
        // MENSAJE ERROR
        // ==========================================
        error.put(
                "mensaje",
                ex.getMessage()
        );

        // ==========================================
        // RETORNAR ERROR 404
        // ==========================================
        return new ResponseEntity<>
                (error, HttpStatus.NOT_FOUND);
    }

    // ======================================================
    // ERROR GENERAL
    // ======================================================
    //
    // Captura cualquier otro error.
    //
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarException(Exception ex) {
        Map<String, String> error =
                new HashMap<>();
        error.put(
                "mensaje",
                "Error interno del servidor"
        );

        return new ResponseEntity<>
                (error, HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}