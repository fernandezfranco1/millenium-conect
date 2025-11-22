package com.millenium.service;

import com.millenium.model.Alumno;
import com.millenium.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AlumnoService {
    
    @Autowired
    private AlumnoRepository alumnoRepository;
    
    public Page<Alumno> findAll(Pageable pageable) {
        return alumnoRepository.findAll(pageable);
    }
    
    public List<Alumno> findAllWithoutPagination() {
        return alumnoRepository.findAll();
    }
    
    public Optional<Alumno> findById(Long id) {
        return alumnoRepository.findById(id);
    }
    
    public Alumno save(Alumno alumno) {
        return alumnoRepository.save(alumno);
    }
    
    public void deleteById(Long id) {
        alumnoRepository.deleteById(id);
    }
    
    public List<Alumno> buscarPorNombreOApellido(String termino) {
        return alumnoRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(termino, termino);
    }
    
    public List<Alumno> findByCategoria(String categoria) {
        return alumnoRepository.findByCategoria(categoria);
    }
    
    public Optional<Alumno> findByDni(String dni) {
        return alumnoRepository.findByDni(dni);
    }
    
    public Page<Alumno> searchAlumnos(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return alumnoRepository.findAll(pageable);
        }
        return alumnoRepository.searchAlumnos(searchTerm.trim(), pageable);
    }
}
