package com.example.Dentmarc.service;

import com.example.Dentmarc.models.Categoria;
import com.example.Dentmarc.models.Producto;
import com.example.Dentmarc.models.Usuario;
import com.example.Dentmarc.models.enums.EstadoProducto;
import com.example.Dentmarc.repository.CategoriaRepository;
import com.example.Dentmarc.repository.MovimientoRepository;
import com.example.Dentmarc.repository.ProductoRepository;
import com.example.Dentmarc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    // Con esta cantidad (o menos) el producto se marca como stock bajo
    public static final int STOCK_BAJO_LIMITE = 20;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Producto> obtenerTodos(){
        return productoRepository.findAll();
    }

    public void guardarProducto(Producto producto){
        productoRepository.save(producto);
    }

    public Producto obtenerProductoPorId(Long id){
        Optional<Producto> productoOptional = productoRepository.findById(id);
        if(productoOptional.isPresent()){
            Producto producto = productoOptional.get();
            return producto;
        }
        return null;
    }

    public Producto obtenerProductoPorSKU(String SKU){
        Optional<Producto> productoOptional = productoRepository.findBySKU(SKU);
        if(productoOptional.isPresent()){
            Producto producto = productoOptional.get();
            return producto;
        }
        return null;
    }

    // ==========================================
    // PANEL DE ADMINISTRACION (Avance 02)
    // ==========================================

    // Filtra por texto (nombre, SKU o marca), categoria y estado; los filtros vacios se ignoran
    public List<Producto> buscarProductos(String busqueda, Long idCategoria, String estado) {
        List<Producto> productos = productoRepository.findAllByOrderByIdDesc();

        if (busqueda != null && !busqueda.isBlank()) {
            String texto = busqueda.trim().toLowerCase();
            productos = productos.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(texto)
                            || p.getSKU().toLowerCase().contains(texto)
                            || p.getMarca().toLowerCase().contains(texto))
                    .toList();
        }

        if (idCategoria != null) {
            productos = productos.stream()
                    .filter(p -> p.getCategoria().getId().equals(idCategoria))
                    .toList();
        }

        if (estado != null && !estado.isBlank()) {
            productos = productos.stream()
                    .filter(p -> p.getEstadoProducto().name().equals(estado))
                    .toList();
        }
        return productos;
    }

    public EstadoProducto calcularEstado(int cantidad) {
        if (cantidad <= 0) {
            return EstadoProducto.AGOTADO;
        }
        if (cantidad <= STOCK_BAJO_LIMITE) {
            return EstadoProducto.STOCK_BAJO;
        }
        return EstadoProducto.ACTIVO;
    }

    // Crea el producto (id null) o actualiza uno existente
    public void guardarProducto(Producto producto, Long idCategoria) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()
                || producto.getMarca() == null || producto.getMarca().isBlank()
                || producto.getSKU() == null || producto.getSKU().isBlank()) {
            throw new IllegalArgumentException("Nombre, marca y SKU son obligatorios");
        }
        if (producto.getPrecio() == null || producto.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (producto.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }

        Categoria categoria = null;
        if (idCategoria != null) {
            categoria = categoriaRepository.findById(idCategoria).orElse(null);
        }
        if (categoria == null) {
            throw new IllegalArgumentException("Selecciona una categoría válida");
        }

        producto.setNombre(producto.getNombre().trim());
        producto.setMarca(producto.getMarca().trim());
        producto.setSKU(producto.getSKU().trim());

        Producto repetido = productoRepository.findBySKU(producto.getSKU()).orElse(null);
        if (repetido != null && !repetido.getId().equals(producto.getId())) {
            throw new IllegalArgumentException("Ya existe un producto con ese SKU");
        }

        producto.setCategoria(categoria);
        producto.setEstadoProducto(calcularEstado(producto.getCantidad()));
        productoRepository.save(producto);
    }

    // Antes de borrar el producto se limpian sus movimientos y los carritos que lo contienen
    @Transactional
    public void eliminarProducto(Long id) {
        for (Usuario usuario : usuarioRepository.findAll()) {
            if (usuario.getCarrito() != null && usuario.getCarrito().removeIf(p -> p.getId().equals(id))) {
                usuarioRepository.save(usuario);
            }
        }
        movimientoRepository.deleteByProducto_Id(id);
        productoRepository.deleteById(id);
    }

    public long contarTotal() {
        return productoRepository.count();
    }

    public long contarPorEstado(EstadoProducto estado) {
        return productoRepository.countByEstadoProducto(estado);
    }

    public long contarConAlerta() {
        return contarPorEstado(EstadoProducto.STOCK_BAJO) + contarPorEstado(EstadoProducto.AGOTADO);
    }

    public long sumarUnidades() {
        return productoRepository.findAll().stream().mapToLong(Producto::getCantidad).sum();
    }

    public double calcularValorInventario() {
        return productoRepository.findAll().stream()
                .mapToDouble(p -> p.getPrecio() * p.getCantidad())
                .sum();
    }

    public List<Producto> obtenerAlertas() {
        return productoRepository.findTop4ByEstadoProductoNotOrderByCantidadAsc(EstadoProducto.ACTIVO);
    }
}
