package com.example.Dentmarc.repository;

import com.example.Dentmarc.models.Usuario;
import com.example.Dentmarc.models.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    List<Optional<Usuario>> findByRol(Rol rol);

    Optional<Usuario> findByNombre(String nombre);
}
