package com.millenium.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "alumnos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alumno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alumno")
    private Long idAlumno;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String apellido;
    
    @NotBlank(message = "El DNI es obligatorio")
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String dni;
    
    @Size(max = 20)
    @Column(length = 20)
    private String telefono;
    
    @DecimalMin(value = "0.0", message = "El peso debe ser positivo")
    private Double peso;
    
    @Size(max = 50)
    @Column(length = 50)
    private String categoria;
    
    @Size(max = 20)
    @Column(length = 20)
    private String disciplina;
    
    @Size(max = 20)
    @Column(length = 20)
    private String genero;
    
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;
    
    @Transient
    private Integer edad;
    
    // Método para calcular la edad automáticamente
    public Integer getEdad() {
        if (fechaNacimiento != null) {
            return Period.between(fechaNacimiento, LocalDate.now()).getYears();
        }
        return null;
    }
}
