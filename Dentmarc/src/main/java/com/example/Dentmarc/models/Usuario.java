package com.example.Dentmarc.models;

import com.example.Dentmarc.models.enums.Rol;
import com.example.Dentmarc.models.enums.TipoPractica;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @Column(nullable = false, unique = true)
    private String email;

    // Siempre se guarda cifrada con BCrypt, nunca en texto plano
    @Column(nullable = false)
    private String contrasenia;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "tipo_practica")
    private TipoPractica tipoPractica;

    @ManyToMany
    @JoinTable(name = "Carrito",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_producto"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Producto> carrito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
}
