package com.millenium.repository;

import com.millenium.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByFechaBetween(LocalDate inicio, LocalDate fin);
    List<Evento> findByTipo(String tipo);
    List<Evento> findByNombreContainingIgnoreCase(String nombre);
}
