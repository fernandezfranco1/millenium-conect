package com.millenium.service;

import com.millenium.model.Alumno;
import com.millenium.model.Clase;
import com.millenium.repository.AlumnoRepository;
import com.millenium.repository.ClaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClaseService {
    
    @Autowired
    private ClaseRepository claseRepository;
    
    @Autowired
    private AlumnoRepository alumnoRepository;
    
    public List<Clase> findAll() {
        return claseRepository.findAll();
    }
    
    public Optional<Clase> findById(Long id) {
        return claseRepository.findById(id);
    }
    
    public List<Clase> findByTipo(String tipo) {
        return claseRepository.findByTipo(tipo);
    }
    
    public List<Clase> findByHorario(LocalTime horario) {
        return claseRepository.findByHorario(horario);
    }
    
    public List<Clase> findByTipoAndHorario(String tipo, LocalTime horario) {
        return claseRepository.findByTipoAndHorario(tipo, horario);
    }
    
    public Clase save(Clase clase) {
        return claseRepository.save(clase);
    }
    
    public void deleteById(Long id) {
        claseRepository.deleteById(id);
    }
    
    public Clase addAlumnoToClase(Long claseId, Long alumnoId) {
        Clase clase = claseRepository.findById(claseId)
            .orElseThrow(() -> new RuntimeException("Clase no encontrada"));
        
        Alumno alumno = alumnoRepository.findById(alumnoId)
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
        
        clase.addAlumno(alumno);
        return claseRepository.save(clase);
    }
    
    public Clase removeAlumnoFromClase(Long claseId, Long alumnoId) {
        Clase clase = claseRepository.findById(claseId)
            .orElseThrow(() -> new RuntimeException("Clase no encontrada"));
        
        Alumno alumno = alumnoRepository.findById(alumnoId)
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
        
        clase.removeAlumno(alumno);
        return claseRepository.save(clase);
    }
}
