package com.example.Dentmarc.service;

import com.example.Dentmarc.models.Usuario;
import com.example.Dentmarc.models.enums.Rol;
import com.example.Dentmarc.models.enums.TipoPractica;
import com.example.Dentmarc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    // Misma regla que el formulario de registro: 8+ caracteres, mayuscula, numero y caracter especial
    private static final String REGLA_CONTRASENIA = "(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}";

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

    // ==========================================
    // PANEL DE ADMINISTRACION (Avance 02)
    // ==========================================

    // rol: ADMINISTRADOR, USUARIO o vacio para todos
    public List<Usuario> buscarUsuarios(String busqueda, String rol) {
        List<Usuario> usuarios = usuarioRepository.findAll();

        if (rol != null && !rol.isBlank()) {
            usuarios = usuarios.stream().filter(u -> u.getRol().name().equals(rol)).toList();
        }

        if (busqueda != null && !busqueda.isBlank()) {
            String texto = busqueda.trim().toLowerCase();
            usuarios = usuarios.stream()
                    .filter(u -> (u.getNombre() + " " + u.getApellidos()).toLowerCase().contains(texto)
                            || u.getEmail().toLowerCase().contains(texto))
                    .toList();
        }
        return usuarios;
    }

    public List<Usuario> buscarClientes(String busqueda) {
        return buscarUsuarios(busqueda, Rol.USUARIO.name());
    }

    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public long contarPorRol(Rol rol) {
        return usuarioRepository.countByRol(rol);
    }

    public List<Usuario> obtenerClientesRecientes() {
        return usuarioRepository.findTop4ByRolOrderByIdDesc(Rol.USUARIO);
    }

    // Crea un usuario nuevo; la contrasenia se cifra con BCrypt antes de guardarla
    public void crearUsuario(Usuario usuario, String contrasenia) {
        validarDatos(usuario);
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }

        validarContrasenia(contrasenia);
        usuario.setContrasenia(passwordEncoder.encode(contrasenia));
        usuario.setCarrito(new ArrayList<>());
        usuarioRepository.save(usuario);
    }

    // Actualiza los datos de un usuario; el correo no se modifica y la contrasenia solo si se escribe una nueva
    public void actualizarUsuario(Long id, Usuario datos, String contrasenia, String emailAdminActual) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        validarDatos(datos);

        boolean esElMismo = usuario.getEmail().equals(emailAdminActual);
        if (esElMismo && datos.getRol() != Rol.ADMINISTRADOR) {
            throw new IllegalArgumentException("No puedes quitarte tu propio rol de administrador");
        }

        usuario.setNombre(datos.getNombre().trim());
        usuario.setApellidos(datos.getApellidos().trim());
        usuario.setTelefono(datos.getTelefono().trim());
        usuario.setTipoPractica(datos.getTipoPractica());
        usuario.setRol(datos.getRol());

        if (contrasenia != null && !contrasenia.isBlank()) {
            validarContrasenia(contrasenia);
            usuario.setContrasenia(passwordEncoder.encode(contrasenia));
        }
        usuarioRepository.save(usuario);
    }

    // Elimina un usuario desde el panel (no permite que un administrador borre su propia cuenta)
    public void eliminarUsuario(Long id, String emailAdminActual) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        if (usuario.getEmail().equals(emailAdminActual)) {
            throw new IllegalArgumentException("No puedes eliminar tu propia cuenta");
        }
        usuarioRepository.deleteById(id);
    }

    private void validarDatos(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()
                || usuario.getApellidos() == null || usuario.getApellidos().isBlank()
                || usuario.getTelefono() == null || usuario.getTelefono().isBlank()) {
            throw new IllegalArgumentException("Nombre, apellidos y teléfono son obligatorios");
        }
        if (usuario.getRol() == null) {
            throw new IllegalArgumentException("Selecciona un rol");
        }
        if (usuario.getTipoPractica() == null) {
            usuario.setTipoPractica(TipoPractica.CONSULTORIO);
        }
    }

    private void validarContrasenia(String contrasenia) {
        if (contrasenia == null || !contrasenia.matches(REGLA_CONTRASENIA)) {
            throw new IllegalArgumentException("La contraseña debe tener 8 o más caracteres, una mayúscula, un número y un carácter especial");
        }
    }
}
