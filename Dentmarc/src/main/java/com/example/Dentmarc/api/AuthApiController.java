package com.example.Dentmarc.api;

import com.example.Dentmarc.api.ApiDtos.AuthResponse;
import com.example.Dentmarc.api.ApiDtos.LoginRequest;
import com.example.Dentmarc.api.ApiDtos.RegisterRequest;
import com.example.Dentmarc.models.Usuario;
import com.example.Dentmarc.models.enums.Rol;
import com.example.Dentmarc.repository.UsuarioRepository;
import com.example.Dentmarc.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApiMapper apiMapper;

    public AuthApiController(UsuarioService usuarioService, UsuarioRepository usuarioRepository,
                             PasswordEncoder passwordEncoder, JwtService jwtService, ApiMapper apiMapper) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.apiMapper = apiMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre().trim());
        usuario.setApellidos(request.apellidos().trim());
        usuario.setEmail(email);
        usuario.setTelefono(request.telefono().trim());
        usuario.setTipoPractica(request.tipoPractica());
        usuario.setRol(Rol.USUARIO);
        usuario.setCarrito(new ArrayList<>());
        usuarioService.crearUsuario(usuario, request.contrasenia());

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse(usuario));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarioService.autenticarUsuario(email, request.contrasenia());
        if (usuario == null || usuario.getRol() != Rol.USUARIO) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.");
        }
        return authResponse(usuario);
    }

    private AuthResponse authResponse(Usuario usuario) {
        return new AuthResponse(jwtService.issueToken(usuario), "Bearer",
                jwtService.getExpirationSeconds(), apiMapper.toUserResponse(usuario));
    }
}
