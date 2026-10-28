package com.example.Dentmarc.models;

import com.example.Dentmarc.models.enums.Rol;
import com.example.Dentmarc.models.enums.TipoPractica;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String contrasenia;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false, name = "tipo_practica")
    private TipoPractica tipoPractica;

    @Column
    private List<Producto> carrito;

    @Column(nullable = false)
    private Rol rol;
}
