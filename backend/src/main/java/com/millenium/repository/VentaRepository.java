package com.millenium.repository;

import com.millenium.model.Venta;
import com.millenium.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByProducto(Producto producto);
    List<Venta> findByFechaVenta(LocalDate fecha);
    List<Venta> findByFechaVentaBetween(LocalDate inicio, LocalDate fin);
}
