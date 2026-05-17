package com.aduanas.comex.notificacion_ms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ERROR PERSONALIZADO
    @ExceptionHandler(
            NotificacionException.class
    )
    public ResponseEntity<Map<String, Object>>
    manejarNotificacionException(
            NotificacionException ex
    ) {

        Map<String, Object> error =
                new HashMap<>();

        error.put(
                "mensaje",
                ex.getMessage()
        );

        error.put(
                "status",
                404
        );

        error.put(
                "fecha",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    // ERROR VALIDACIONES DTO
    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<Map<String, Object>>
    manejarValidaciones(
            MethodArgumentNotValidException ex
    ) {

        Map<String, Object> error =
                new HashMap<>();

        error.put(
                "mensaje",
                "Error de validación"
        );

        error.put(
                "detalle",
                ex.getBindingResult()
                        .getFieldError()
                        .getDefaultMessage()
        );

        error.put(
                "status",
                400
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }
}