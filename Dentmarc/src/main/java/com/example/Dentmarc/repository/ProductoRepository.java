package com.example.Dentmarc.repository;

import com.example.Dentmarc.models.Categoria;
import com.example.Dentmarc.models.Producto;
import com.example.Dentmarc.models.enums.EstadoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByNombre(String nombre);

    Optional<Producto> findBySKU(String SKU);

    List<Producto> findByEstadoProducto(EstadoProducto estadoProducto);

    // Antes era findByCategoriaProducto(CategoriaProducto): la categoria ahora es una entidad
    List<Producto> findByCategoria(Categoria categoria);

    List<Producto> findAllByOrderByIdDesc();

    long countByEstadoProducto(EstadoProducto estadoProducto);

    long countByCategoria_Id(Long idCategoria);

    // Productos que necesitan reposicion (los que menos stock tienen primero)
    List<Producto> findTop4ByEstadoProductoNotOrderByCantidadAsc(EstadoProducto estadoProducto);
}
