package com.example.Dentmarc.service;

import com.example.Dentmarc.models.Movimiento;
import com.example.Dentmarc.models.Producto;
import com.example.Dentmarc.models.enums.TipoMovimiento;
import com.example.Dentmarc.repository.MovimientoRepository;
import com.example.Dentmarc.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventarioService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private ProductoService productoService;

    // ENTRADA suma, SALIDA resta y AJUSTE deja el stock exactamente en la cantidad indicada
    @Transactional
    public void registrarMovimiento(Long idProducto, TipoMovimiento tipo, int cantidad) {
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto == null) {
            throw new IllegalArgumentException("El producto no existe");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Selecciona el tipo de movimiento");
        }

        int nuevoStock;
        int diferencia;
        if (tipo == TipoMovimiento.AJUSTE) {
            if (cantidad < 0) {
                throw new IllegalArgumentException("El stock ajustado no puede ser negativo");
            }
            nuevoStock = cantidad;
            diferencia = cantidad - producto.getCantidad();
        } else {
            if (cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
            }
            diferencia = (tipo == TipoMovimiento.ENTRADA) ? cantidad : -cantidad;
            nuevoStock = producto.getCantidad() + diferencia;
            if (nuevoStock < 0) {
                throw new IllegalArgumentException("No hay suficiente stock para registrar esa salida");
            }
        }

        producto.setCantidad(nuevoStock);
        producto.setEstadoProducto(productoService.calcularEstado(nuevoStock));
        productoRepository.save(producto);

        Movimiento movimiento = new Movimiento();
        movimiento.setProducto(producto);
        movimiento.setTipo(tipo);
        movimiento.setCantidad(diferencia);
        movimiento.setFecha(LocalDateTime.now());
        movimientoRepository.save(movimiento);
    }

    // Mapa idProducto -> ultimo movimiento (solo los productos que tienen alguno)
    public Map<Long, Movimiento> obtenerUltimosMovimientos(List<Producto> productos) {
        Map<Long, Movimiento> ultimos = new HashMap<>();
        for (Producto producto : productos) {
            Movimiento movimiento = movimientoRepository
                    .findFirstByProducto_IdOrderByFechaDesc(producto.getId()).orElse(null);
            if (movimiento != null) {
                ultimos.put(producto.getId(), movimiento);
            }
        }
        return ultimos;
    }

    public List<Movimiento> obtenerMovimientosRecientes() {
        return movimientoRepository.findTop5ByOrderByFechaDesc();
    }
}
