package com.millenium.controller;

import com.millenium.model.Cuota;
import com.millenium.service.CuotaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cuotas")
@CrossOrigin(origins = "*")
public class CuotaController {
    
    @Autowired
    private CuotaService cuotaService;
    
    @Value("${upload.path:uploads/comprobantes}")
    private String uploadPath;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaPago") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Cuota> cuotasPage = cuotaService.findAll(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", cuotasPage.getContent());
        response.put("currentPage", cuotasPage.getNumber());
        response.put("totalItems", cuotasPage.getTotalElements());
        response.put("totalPages", cuotasPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Cuota>> getAllNonPaginated() {
        return ResponseEntity.ok(cuotaService.findAllNonPaginated());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Cuota> getById(@PathVariable Long id) {
        return cuotaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Cuota> create(@Valid @RequestBody Cuota cuota) {
        Cuota saved = cuotaService.save(cuota);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadComprobante(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("error", "El archivo está vacío");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validar tipo de archivo
            String contentType = file.getContentType();
            if (contentType == null || 
                (!contentType.equals("application/pdf") && 
                 !contentType.startsWith("image/"))) {
                response.put("error", "Solo se permiten archivos PDF o imágenes");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Crear directorio si no existe
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // Generar nombre único para el archivo
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
            String filename = UUID.randomUUID().toString() + extension;
            
            // Guardar archivo
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            response.put("filename", filename);
            response.put("message", "Archivo subido correctamente");
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("error", "Error al subir el archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Cuota> update(@PathVariable Long id, @Valid @RequestBody Cuota cuota) {
        return cuotaService.findById(id)
                .map(existing -> {
                    cuota.setIdCuota(id);
                    return ResponseEntity.ok(cuotaService.save(cuota));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (cuotaService.findById(id).isPresent()) {
            cuotaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/rango")
    public ResponseEntity<List<Cuota>> getByRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(cuotaService.findByRangoFechas(inicio, fin));
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Cuota>> getByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(cuotaService.findByEstado(estado));
    }
}
