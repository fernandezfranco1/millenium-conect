package com.millenium.controller;

import com.millenium.model.Asistencia;
import com.millenium.service.AsistenciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
@CrossOrigin(origins = "*")
public class AsistenciaController {
    
    @Autowired
    private AsistenciaService asistenciaService;
    
    @GetMapping
    public ResponseEntity<List<Asistencia>> getAll() {
        return ResponseEntity.ok(asistenciaService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Asistencia> getById(@PathVariable Long id) {
        return asistenciaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Asistencia> create(@Valid @RequestBody Asistencia asistencia) {
        Asistencia saved = asistenciaService.save(asistencia);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Asistencia> update(@PathVariable Long id, @Valid @RequestBody Asistencia asistencia) {
        return asistenciaService.findById(id)
                .map(existing -> {
                    asistencia.setIdAsistencia(id);
                    return ResponseEntity.ok(asistenciaService.save(asistencia));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (asistenciaService.findById(id).isPresent()) {
            asistenciaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/fecha")
    public ResponseEntity<List<Asistencia>> getByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(asistenciaService.findByFecha(fecha));
    }
    
    @GetMapping("/rango")
    public ResponseEntity<List<Asistencia>> getByRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(asistenciaService.findByRangoFechas(inicio, fin));
    }
}
