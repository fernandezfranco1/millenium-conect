package com.millenium.service;

import com.millenium.model.Cuota;
import com.millenium.repository.CuotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CuotaService {
    
    @Autowired
    private CuotaRepository cuotaRepository;
    
    public Page<Cuota> findAll(Pageable pageable) {
        Page<Cuota> cuotas = cuotaRepository.findAll(pageable);
        // Forzar la carga de los alumnos
        cuotas.forEach(c -> c.getAlumno().getNombre());
        return cuotas;
    }
    
    public List<Cuota> findAllNonPaginated() {
        List<Cuota> cuotas = cuotaRepository.findAll();
        // Forzar la carga de los alumnos
        cuotas.forEach(c -> c.getAlumno().getNombre());
        return cuotas;
    }
    
    public Optional<Cuota> findById(Long id) {
        return cuotaRepository.findById(id);
    }
    
    public Cuota save(Cuota cuota) {
        return cuotaRepository.save(cuota);
    }
    
    public void deleteById(Long id) {
        cuotaRepository.deleteById(id);
    }
    
    public List<Cuota> findByRangoFechas(LocalDate inicio, LocalDate fin) {
        return cuotaRepository.findByFechaPagoBetween(inicio, fin);
    }
    
    public List<Cuota> findByEstado(String estado) {
        return cuotaRepository.findByEstado(estado);
    }
    
    public Page<Cuota> findByFilters(String estado, String formaPago, LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable) {
        Page<Cuota> cuotas;
        
        if (estado != null && formaPago != null && fechaInicio != null && fechaFin != null) {
            cuotas = cuotaRepository.findByEstadoAndFormaPagoAndFechaPagoBetween(estado, formaPago, fechaInicio, fechaFin, pageable);
        } else if (estado != null && formaPago != null) {
            cuotas = cuotaRepository.findByEstadoAndFormaPago(estado, formaPago, pageable);
        } else if (estado != null && fechaInicio != null && fechaFin != null) {
            cuotas = cuotaRepository.findByEstadoAndFechaPagoBetween(estado, fechaInicio, fechaFin, pageable);
        } else if (formaPago != null && fechaInicio != null && fechaFin != null) {
            cuotas = cuotaRepository.findByFormaPagoAndFechaPagoBetween(formaPago, fechaInicio, fechaFin, pageable);
        } else if (estado != null) {
            cuotas = cuotaRepository.findByEstado(estado, pageable);
        } else if (formaPago != null) {
            cuotas = cuotaRepository.findByFormaPago(formaPago, pageable);
        } else if (fechaInicio != null && fechaFin != null) {
            cuotas = cuotaRepository.findByFechaPagoBetween(fechaInicio, fechaFin, pageable);
        } else {
            cuotas = cuotaRepository.findAll(pageable);
        }
        
        // Forzar la carga de los alumnos
        cuotas.forEach(c -> c.getAlumno().getNombre());
        return cuotas;
    }
}
