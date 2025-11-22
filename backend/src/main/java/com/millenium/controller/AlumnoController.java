package com.millenium.controller;

import com.millenium.model.Alumno;
import com.millenium.service.AlumnoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alumnos")
@CrossOrigin(origins = "*")
public class AlumnoController {
    
    @Autowired
    private AlumnoService alumnoService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idAlumno") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Alumno> alumnosPage = alumnoService.findAll(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", alumnosPage.getContent());
        response.put("currentPage", alumnosPage.getNumber());
        response.put("totalItems", alumnosPage.getTotalElements());
        response.put("totalPages", alumnosPage.getTotalPages());
        response.put("hasNext", alumnosPage.hasNext());
        response.put("hasPrevious", alumnosPage.hasPrevious());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Alumno>> getAllWithoutPagination() {
        return ResponseEntity.ok(alumnoService.findAllWithoutPagination());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Alumno> getById(@PathVariable Long id) {
        return alumnoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Alumno> create(@Valid @RequestBody Alumno alumno) {
        Alumno saved = alumnoService.save(alumno);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Alumno> update(@PathVariable Long id, @Valid @RequestBody Alumno alumno) {
        return alumnoService.findById(id)
                .map(existing -> {
                    alumno.setIdAlumno(id);
                    return ResponseEntity.ok(alumnoService.save(alumno));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (alumnoService.findById(id).isPresent()) {
            alumnoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Alumno>> buscar(@RequestParam String termino) {
        return ResponseEntity.ok(alumnoService.buscarPorNombreOApellido(termino));
    }
    
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Alumno>> getByCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(alumnoService.findByCategoria(categoria));
    }
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchAlumnos(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        Page<Alumno> alumnosPage = alumnoService.searchAlumnos(q, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", alumnosPage.getContent());
        response.put("currentPage", alumnosPage.getNumber());
        response.put("totalItems", alumnosPage.getTotalElements());
        response.put("totalPages", alumnosPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
}
