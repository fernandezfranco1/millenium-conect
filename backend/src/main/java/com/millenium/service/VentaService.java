package com.millenium.service;

import com.millenium.model.Venta;
import com.millenium.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VentaService {
    
    @Autowired
    private VentaRepository ventaRepository;
    
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }
    
    public Optional<Venta> findById(Long id) {
        return ventaRepository.findById(id);
    }
    
    public Venta save(Venta venta) {
        return ventaRepository.save(venta);
    }
    
    public void deleteById(Long id) {
        ventaRepository.deleteById(id);
    }
    
    public List<Venta> findByRangoFechas(LocalDate inicio, LocalDate fin) {
        return ventaRepository.findByFechaVentaBetween(inicio, fin);
    }
}
