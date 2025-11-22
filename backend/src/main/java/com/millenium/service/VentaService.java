package com.millenium.service;

import com.millenium.model.Producto;
import com.millenium.model.Venta;
import com.millenium.repository.ProductoRepository;
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
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private EmailService emailService;
    
    private static final int STOCK_MINIMO = 3;
    
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }
    
    public Optional<Venta> findById(Long id) {
        return ventaRepository.findById(id);
    }
    
    public Venta save(Venta venta) {
        // Si es una venta nueva, descontar del stock
        if (venta.getIdVenta() == null) {
            // Cargar el producto desde la base de datos
            Producto producto = productoRepository.findById(venta.getProducto().getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            // Verificar que hay stock suficiente
            if (producto.getStock() < venta.getCantidad()) {
                throw new RuntimeException("Stock insuficiente. Stock disponible: " + producto.getStock());
            }
            
            // Descontar del stock
            producto.setStock(producto.getStock() - venta.getCantidad());
            productoRepository.save(producto);
            
            // Verificar si el stock quedó bajo y enviar alerta
            if (producto.getStock() < STOCK_MINIMO) {
                try {
                    emailService.sendLowStockAlert(producto);
                } catch (Exception e) {
                    System.err.println("Error al enviar alerta de stock: " + e.getMessage());
                }
            }
            
            // Asignar el producto completo a la venta
            venta.setProducto(producto);
        }
        
        return ventaRepository.save(venta);
    }
    
    public void deleteById(Long id) {
        // Al eliminar una venta, devolver el stock al producto
        Optional<Venta> ventaOpt = ventaRepository.findById(id);
        if (ventaOpt.isPresent()) {
            Venta venta = ventaOpt.get();
            Producto producto = venta.getProducto();
            
            // Devolver el stock
            producto.setStock(producto.getStock() + venta.getCantidad());
            productoRepository.save(producto);
            
            ventaRepository.deleteById(id);
        }
    }
    
    public List<Venta> findByRangoFechas(LocalDate inicio, LocalDate fin) {
        return ventaRepository.findByFechaVentaBetween(inicio, fin);
    }
}
