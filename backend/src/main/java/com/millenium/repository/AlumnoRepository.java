package com.millenium.repository;

import com.millenium.model.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    Optional<Alumno> findByDni(String dni);
    List<Alumno> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);
    List<Alumno> findByCategoria(String categoria);
    
    @Query("SELECT a FROM Alumno a WHERE " +
           "LOWER(a.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.apellido) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "a.dni LIKE CONCAT('%', :searchTerm, '%')")
    Page<Alumno> searchAlumnos(@Param("searchTerm") String searchTerm, Pageable pageable);
}
