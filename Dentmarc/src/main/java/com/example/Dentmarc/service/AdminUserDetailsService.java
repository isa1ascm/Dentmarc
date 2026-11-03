package com.example.Dentmarc.service;

import com.example.Dentmarc.models.Usuario;
import com.example.Dentmarc.models.enums.Rol;
import com.example.Dentmarc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Valida las credenciales del panel contra la base de datos (solo entran administradores)
@Service
public class AdminUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            throw new UsernameNotFoundException("El usuario no es administrador");
        }

        return User.withUsername(usuario.getEmail())
                .password(usuario.getContrasenia())
                .roles(usuario.getRol().name())
                .build();
    }
}
