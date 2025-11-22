package com.millenium.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cuotas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuota {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuota")
    private Long idCuota;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_alumno", nullable = false)
    @NotNull(message = "El alumno es obligatorio")
    private Alumno alumno;
    
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", message = "El monto debe ser positivo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    
    @NotNull(message = "La fecha de pago es obligatoria")
    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;
    
    @NotBlank(message = "El mes de pago es obligatorio")
    @Size(max = 7)
    @Column(name = "mes_pago", nullable = false, length = 7)
    private String mesPago; // Formato: YYYY-MM (2025-01)
    
    @NotBlank(message = "La forma de pago es obligatoria")
    @Size(max = 50)
    @Column(name = "forma_pago", nullable = false, length = 50)
    private String formaPago; // Débito, Efectivo, Transferencia, Crédito
    
    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String estado; // Pagado, Pendiente
    
    @Column(length = 255)
    private String comprobante; // Nombre del archivo o URL del comprobante
}
