package com.millenium.controller;

import com.millenium.model.Clase;
import com.millenium.service.ClaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/clases")
@CrossOrigin(origins = "*")
public class ClaseController {
    
    @Autowired
    private ClaseService claseService;
    
    @GetMapping
    public List<Clase> getAll() {
        return claseService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Clase> getById(@PathVariable Long id) {
        return claseService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/tipo/{tipo}")
    public List<Clase> getByTipo(@PathVariable String tipo) {
        return claseService.findByTipo(tipo);
    }
    
    @GetMapping("/horario/{horario}")
    public List<Clase> getByHorario(@PathVariable String horario) {
        LocalTime time = LocalTime.parse(horario);
        return claseService.findByHorario(time);
    }
    
    @GetMapping("/filtrar")
    public List<Clase> filtrar(
        @RequestParam(required = false) String tipo,
        @RequestParam(required = false) String horario
    ) {
        if (tipo != null && horario != null) {
            LocalTime time = LocalTime.parse(horario);
            return claseService.findByTipoAndHorario(tipo, time);
        } else if (tipo != null) {
            return claseService.findByTipo(tipo);
        } else if (horario != null) {
            LocalTime time = LocalTime.parse(horario);
            return claseService.findByHorario(time);
        }
        return claseService.findAll();
    }
    
    @PostMapping
    public ResponseEntity<Clase> create(@RequestBody Clase clase) {
        try {
            Clase nuevaClase = claseService.save(clase);
            return ResponseEntity.ok(nuevaClase);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Clase> update(@PathVariable Long id, @RequestBody Clase clase) {
        return claseService.findById(id)
            .map(claseExistente -> {
                clase.setIdClase(id);
                clase.setAlumnos(claseExistente.getAlumnos());
                return ResponseEntity.ok(claseService.save(clase));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (claseService.findById(id).isPresent()) {
            claseService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{claseId}/alumnos/{alumnoId}")
    public ResponseEntity<Clase> addAlumno(
        @PathVariable Long claseId,
        @PathVariable Long alumnoId
    ) {
        try {
            Clase clase = claseService.addAlumnoToClase(claseId, alumnoId);
            return ResponseEntity.ok(clase);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{claseId}/alumnos/{alumnoId}")
    public ResponseEntity<Clase> removeAlumno(
        @PathVariable Long claseId,
        @PathVariable Long alumnoId
    ) {
        try {
            Clase clase = claseService.removeAlumnoFromClase(claseId, alumnoId);
            return ResponseEntity.ok(clase);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
