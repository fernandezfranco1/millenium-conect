package com.millenium.service;

import com.millenium.model.Evento;
import com.millenium.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventoService {
    
    @Autowired
    private EventoRepository eventoRepository;
    
    public List<Evento> findAll() {
        return eventoRepository.findAll();
    }
    
    public Optional<Evento> findById(Long id) {
        return eventoRepository.findById(id);
    }
    
    public Evento save(Evento evento) {
        return eventoRepository.save(evento);
    }
    
    public void deleteById(Long id) {
        eventoRepository.deleteById(id);
    }
    
    public List<Evento> findByTipo(String tipo) {
        return eventoRepository.findByTipo(tipo);
    }
    
    public List<Evento> findByRangoFechas(LocalDate inicio, LocalDate fin) {
        return eventoRepository.findByFechaBetween(inicio, fin);
    }
}
