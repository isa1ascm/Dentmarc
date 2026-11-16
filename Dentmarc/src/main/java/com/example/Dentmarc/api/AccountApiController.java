package com.example.Dentmarc.api;

import com.example.Dentmarc.api.ApiDtos.UserResponse;
import com.example.Dentmarc.models.Usuario;
import com.example.Dentmarc.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
public class AccountApiController {
    private final UsuarioRepository usuarioRepository;
    private final ApiMapper apiMapper;

    public AccountApiController(UsuarioRepository usuarioRepository, ApiMapper apiMapper) {
        this.usuarioRepository = usuarioRepository;
        this.apiMapper = apiMapper;
    }

    @GetMapping
    public UserResponse get(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No se encontró la cuenta."));
        return apiMapper.toUserResponse(usuario);
    }
}
