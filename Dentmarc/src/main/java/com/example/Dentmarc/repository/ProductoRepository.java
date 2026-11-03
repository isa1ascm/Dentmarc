package com.example.Dentmarc.repository;

import com.example.Dentmarc.models.Producto;
import com.example.Dentmarc.models.enums.CategoriaProducto;
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

    List<Producto> findByCategoriaProducto(CategoriaProducto categoriaProducto);
}
