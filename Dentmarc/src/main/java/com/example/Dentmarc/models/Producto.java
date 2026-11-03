package com.example.Dentmarc.models;

import com.example.Dentmarc.models.enums.CategoriaProducto;
import com.example.Dentmarc.models.enums.EstadoProducto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private String SKU;

    @Column(nullable = false, name = "estado_producto")
    private EstadoProducto estadoProducto;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false, name = "categoria_producto")
    private CategoriaProducto categoriaProducto;
}
