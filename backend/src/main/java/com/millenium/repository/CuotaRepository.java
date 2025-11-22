package com.millenium.repository;

import com.millenium.model.Cuota;
import com.millenium.model.Alumno;
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
}
