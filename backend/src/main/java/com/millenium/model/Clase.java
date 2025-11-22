package com.millenium.model;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clases")
public class Clase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_clase")
    private Long idClase;
    
    @Column(name = "instructor", nullable = false, length = 100)
    private String instructor;
    
    @Column(name = "apellido_instructor", nullable = false, length = 100)
    private String apellidoInstructor;
    
    @Column(name = "horario", nullable = false)
    private LocalTime horario;
    
    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "clase_alumno",
        joinColumns = @JoinColumn(name = "id_clase"),
        inverseJoinColumns = @JoinColumn(name = "id_alumno")
    )
    private Set<Alumno> alumnos = new HashSet<>();
    
    // Constructores
    public Clase() {}
    
    public Clase(String instructor, String apellidoInstructor, LocalTime horario, String tipo) {
        this.instructor = instructor;
        this.apellidoInstructor = apellidoInstructor;
        this.horario = horario;
        this.tipo = tipo;
    }
    
    // Getters y Setters
    public Long getIdClase() {
        return idClase;
    }
    
    public void setIdClase(Long idClase) {
        this.idClase = idClase;
    }
    
    public String getInstructor() {
        return instructor;
    }
    
    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }
    
    public String getApellidoInstructor() {
        return apellidoInstructor;
    }
    
    public void setApellidoInstructor(String apellidoInstructor) {
        this.apellidoInstructor = apellidoInstructor;
    }
    
    public LocalTime getHorario() {
        return horario;
    }
    
    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public Set<Alumno> getAlumnos() {
        return alumnos;
    }
    
    public void setAlumnos(Set<Alumno> alumnos) {
        this.alumnos = alumnos;
    }
    
    // Métodos auxiliares
    public void addAlumno(Alumno alumno) {
        this.alumnos.add(alumno);
    }
    
    public void removeAlumno(Alumno alumno) {
        this.alumnos.remove(alumno);
    }
}
