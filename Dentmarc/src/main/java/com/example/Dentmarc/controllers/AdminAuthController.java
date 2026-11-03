package com.example.Dentmarc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// El POST de /admin/login y /admin/logout lo procesa Spring Security (ver SeguridadConfig)
@Controller
public class AdminAuthController {

    @GetMapping("/admin/login")
    public String mostrarLogin() {
        return "login-admin";
    }

    @GetMapping("/admin")
    public String admin() {
        return "redirect:/admin/resumen";
    }
}
