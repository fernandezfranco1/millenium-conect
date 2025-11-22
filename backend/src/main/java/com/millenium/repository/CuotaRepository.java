package com.millenium.repository;

import com.millenium.model.Cuota;
import com.millenium.model.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Long> {
    List<Cuota> findByAlumno(Alumno alumno);
    List<Cuota> findByEstado(String estado);
    List<Cuota> findByFechaPagoBetween(LocalDate inicio, LocalDate fin);
    List<Cuota> findByFormaPago(String formaPago);
    
    // Métodos paginados para filtros
    Page<Cuota> findByEstado(String estado, Pageable pageable);
    Page<Cuota> findByFormaPago(String formaPago, Pageable pageable);
    Page<Cuota> findByFechaPagoBetween(LocalDate inicio, LocalDate fin, Pageable pageable);
    Page<Cuota> findByEstadoAndFormaPago(String estado, String formaPago, Pageable pageable);
    Page<Cuota> findByEstadoAndFechaPagoBetween(String estado, LocalDate inicio, LocalDate fin, Pageable pageable);
    Page<Cuota> findByFormaPagoAndFechaPagoBetween(String formaPago, LocalDate inicio, LocalDate fin, Pageable pageable);
    Page<Cuota> findByEstadoAndFormaPagoAndFechaPagoBetween(String estado, String formaPago, LocalDate inicio, LocalDate fin, Pageable pageable);
}
