package com.millenium.repository;

import com.millenium.model.Asistencia;
import com.millenium.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByAlumno(Alumno alumno);
    List<Asistencia> findByFechaAsistencia(LocalDate fecha);
    List<Asistencia> findByFechaAsistenciaBetween(LocalDate inicio, LocalDate fin);
    List<Asistencia> findByEstado(String estado);
}
