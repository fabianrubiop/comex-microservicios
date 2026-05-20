package com.aduanas.com.pagosms.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice // <--- ¡ESTA WEA ES OBLIGATORIA! Le avisa a Spring Boot que intercepte los errores de todos los controladores
public class GlobalExceptionHandler {

    // =========================================================================
    // 1. CAPTURA CUANDO EL JSON ESTÁ MALO (Errores de @Valid)
    // =========================================================================
    // Si mandan un monto vacío, monedas inválidas o un cargaId nulo,
    // este método lo frena en seco y devuelve un mapa limpio con el campo y el porqué.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }

    // =========================================================================
    // 2. CAPTURA ERRORES DE NEGOCIO (RuntimeException)
    // =========================================================================
    // Captura tus "throw new RuntimeException(...)" personalizados del Service.
    // Ej: "La carga con ID X no existe" o "Pago completado, pero falló Feign con Clasificación"
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage()));
    }
}