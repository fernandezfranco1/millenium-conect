package com.millenium.service;

import com.millenium.model.Asistencia;
import com.millenium.repository.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AsistenciaService {
    
    @Autowired
    private AsistenciaRepository asistenciaRepository;
    
    public List<Asistencia> findAll() {
        List<Asistencia> asistencias = asistenciaRepository.findAll();
        // Forzar la carga de los alumnos
        asistencias.forEach(a -> a.getAlumno().getNombre());
        return asistencias;
    }
    
    public Optional<Asistencia> findById(Long id) {
        return asistenciaRepository.findById(id);
    }
    
    public Asistencia save(Asistencia asistencia) {
        return asistenciaRepository.save(asistencia);
    }
    
    public void deleteById(Long id) {
        asistenciaRepository.deleteById(id);
    }
    
    public List<Asistencia> findByFecha(LocalDate fecha) {
        return asistenciaRepository.findByFechaAsistencia(fecha);
    }
    
    public List<Asistencia> findByRangoFechas(LocalDate inicio, LocalDate fin) {
        return asistenciaRepository.findByFechaAsistenciaBetween(inicio, fin);
    }
}
