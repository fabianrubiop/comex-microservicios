package com.aduanas.com.pagosms.entity;

import com.aduanas.com.pagosms.Enum.EstadoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="pagos")


public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pagoId;

    @Column(nullable = false)
    private Long cargaId;

    //¿Por qué BigDecimal?
    //Precisión absoluta: No redondea al azar. Tú controlas cuántos decimales quieres.
    //Control financiero: Es el estándar en bancos y sistemas contables.
    //precision = 10: Es el número total de dígitos que puede tener el número
    // (ejemplo: 12345678.90 tiene 10 dígitos).
    //scale = 2: Es cuántos de esos dígitos son decimales
    // (en este caso, 2 para los centavos).
    //Aquí es donde cambia un poco la jugada, porque BigDecimal no es un número primitivo, es un objeto:
    //Se hace así: monto = new BigDecimal("10.50"); o BigDecimal.valueOf(10.50);
    //Para sumar: No usas +, usas .add() -> monto.add(otroMonto);
    // float / double: Malo para plata, bueno para medir la velocidad de un viento o la temperatura.
    //BigDecimal: El rey para precios, impuestos y saldos.
    @Column(name = "monto", precision = 10, scale = 2)
    private BigDecimal monto;


    @Column(name = "moneda", length = 3)
    private String moneda; // Aquí puedes usar String si guardas el código ISO (USD, CLP, MXN)

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago")
    private EstadoPago estadoPago; // Otro Enum para controlar los estados

    @Column(name = "transaccion_externa_id", unique = true)
    //Le puse unique = true porque normalmente esos IDs que te da PayPal, Stripe o el banco son únicos.
    // Esto evita que por un error de código metas el mismo pago dos veces.
    private String transaccionExternaId; // "unique" porque los IDs de transacciones no se deben repetir

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago; // LocalDateTime es el estándar para marcas de tiempo


}
