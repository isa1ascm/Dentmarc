package com.example.Dentmarc.controllers;

import com.example.Dentmarc.models.Usuario;
import com.example.Dentmarc.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ClienteController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/admin/clientes")
    public String listarClientes(@RequestParam(name = "buscar", required = false) String buscar, Model model) {
        model.addAttribute("clientes", usuarioService.buscarClientes(buscar));
        model.addAttribute("buscar", buscar);
        return "clientes-admin";
    }

    @PostMapping("/admin/clientes/eliminar/{id}")
    public String eliminarCliente(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
        Usuario cliente = usuarioService.obtenerUsuarioPorId(id);
        if (cliente == null) {
            redirectAttributes.addFlashAttribute("error", "El cliente no existe");
            return "redirect:/admin/clientes";
        }

        try {
            usuarioService.eliminarUsuario(id, principal.getName());
            redirectAttributes.addFlashAttribute("mensaje", "Cliente eliminado correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/clientes";
    }
}
