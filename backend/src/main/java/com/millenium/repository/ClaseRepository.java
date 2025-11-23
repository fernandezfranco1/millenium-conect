package com.millenium.repository;

import com.millenium.model.Clase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {
    
    List<Clase> findByTipo(String tipo);
    
    List<Clase> findByHorario(LocalTime horario);
    
    @Query("SELECT c FROM Clase c WHERE c.tipo = :tipo AND c.horario = :horario")
    List<Clase> findByTipoAndHorario(@Param("tipo") String tipo, @Param("horario") LocalTime horario);
    
    @Query("SELECT c FROM Clase c JOIN c.alumnos a WHERE a.idAlumno = :alumnoId")
    List<Clase> findByAlumnoId(@Param("alumnoId") Long alumnoId);
}
