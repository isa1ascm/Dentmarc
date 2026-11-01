package com.example.Dentmarc.service;

import com.example.Dentmarc.models.Usuario;
import com.example.Dentmarc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> obtenerTodos(){
        return usuarioRepository.findAll();
    }

    public void registrarUsuario(Usuario usuario){
        usuario.setContrasenia(passwordEncoder.encode(usuario.getContrasenia()));
        usuarioRepository.save(usuario);
    }

    public Usuario obtenerUsuarioPorId(Long id){
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if(usuarioOptional.isPresent()){
            Usuario usuario = usuarioOptional.get();
            return usuario;
        }
        return null;
    }

    public void eliminarUsuarioPorId(Long id){
        usuarioRepository.deleteById(id);
    }

    public Usuario autenticarUsuario(String email, String password){
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if(usuarioOpt.isPresent()){
            Usuario usuario = usuarioOpt.get();
            if(passwordEncoder.matches(password, usuario.getContrasenia())){
                return usuario;
            }
        }
        return null;
    }
}
