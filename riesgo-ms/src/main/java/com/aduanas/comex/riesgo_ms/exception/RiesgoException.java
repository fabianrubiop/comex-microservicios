package com.aduanas.comex.riesgo_ms.exception;

// ======================================================
// ================= RIESGO EXCEPTION ===================
// ======================================================
//
// Esta es una excepción personalizada.
//
// Sirve para lanzar errores propios
// del microservicio.
//
// Ejemplo:
//
// throw new RiesgoException(
//      "Riesgo no encontrado"
// );
//
public class RiesgoException extends RuntimeException {

    // ======================================================
    // ================= CONSTRUCTOR =========================
    // ======================================================
    //
    // Recibe mensaje error.
    //
    public RiesgoException(String mensaje) {
        super(mensaje);
    }
}
