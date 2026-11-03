package com.example.Dentmarc.controllers;

import com.example.Dentmarc.models.Usuario;
import com.example.Dentmarc.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

// Deja disponible al administrador en sesion para el menu lateral (nombre, iniciales y rol)
@ControllerAdvice
public class AdminControllerAdvice {

    @Autowired
    private UsuarioService usuarioService;

    @ModelAttribute("adminActual")
    public Usuario adminActual(Principal principal) {
        if (principal == null) {
            return null;
        }
        return usuarioService.obtenerPorEmail(principal.getName());
    }
}
