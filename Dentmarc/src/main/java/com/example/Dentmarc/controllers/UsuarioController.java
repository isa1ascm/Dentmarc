package com.example.Dentmarc.controllers;

import com.example.Dentmarc.models.Usuario;
import com.example.Dentmarc.models.enums.Rol;
import com.example.Dentmarc.models.enums.TipoPractica;
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
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/admin/usuarios")
    public String listarUsuarios(@RequestParam(name = "buscar", required = false) String buscar,
                                 @RequestParam(name = "rol", required = false) String rol,
                                 Model model) {
        model.addAttribute("usuarios", usuarioService.buscarUsuarios(buscar, rol));
        model.addAttribute("buscar", buscar);
        model.addAttribute("rolSeleccionado", rol);
        model.addAttribute("totalAdministradores", usuarioService.contarPorRol(Rol.ADMINISTRADOR));
        model.addAttribute("totalClientes", usuarioService.contarPorRol(Rol.USUARIO));
        return "usuarios-admin";
    }

    @GetMapping("/admin/usuarios/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        cargarOpciones(model);
        return "usuario-form-admin";
    }

    @PostMapping("/admin/usuarios/nuevo")
    public String guardarUsuario(@RequestParam("nombre") String nombre,
                                 @RequestParam("apellidos") String apellidos,
                                 @RequestParam("email") String email,
                                 @RequestParam("telefono") String telefono,
                                 @RequestParam("tipoPractica") TipoPractica tipoPractica,
                                 @RequestParam("rol") Rol rol,
                                 @RequestParam("contrasenia") String contrasenia,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setTipoPractica(tipoPractica);
        usuario.setRol(rol);

        try {
            usuarioService.crearUsuario(usuario, contrasenia);
        } catch (IllegalArgumentException e) {
            model.addAttribute("usuario", usuario);
            cargarOpciones(model);
            model.addAttribute("error", e.getMessage());
            return "usuario-form-admin";
        }

        redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "El usuario no existe");
            return "redirect:/admin/usuarios";
        }

        model.addAttribute("usuario", usuario);
        cargarOpciones(model);
        return "usuario-form-admin";
    }

    @PostMapping("/admin/usuarios/editar/{id}")
    public String actualizarUsuario(@PathVariable("id") Long id,
                                    @RequestParam("nombre") String nombre,
                                    @RequestParam("apellidos") String apellidos,
                                    @RequestParam("telefono") String telefono,
                                    @RequestParam("tipoPractica") TipoPractica tipoPractica,
                                    @RequestParam("rol") Rol rol,
                                    @RequestParam(name = "contrasenia", required = false) String contrasenia,
                                    Principal principal,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        Usuario existente = usuarioService.obtenerUsuarioPorId(id);
        if (existente == null) {
            redirectAttributes.addFlashAttribute("error", "El usuario no existe");
            return "redirect:/admin/usuarios";
        }

        Usuario datos = new Usuario();
        datos.setId(id);
        datos.setEmail(existente.getEmail());
        datos.setNombre(nombre);
        datos.setApellidos(apellidos);
        datos.setTelefono(telefono);
        datos.setTipoPractica(tipoPractica);
        datos.setRol(rol);

        try {
            usuarioService.actualizarUsuario(id, datos, contrasenia, principal.getName());
        } catch (IllegalArgumentException e) {
            model.addAttribute("usuario", datos);
            cargarOpciones(model);
            model.addAttribute("error", e.getMessage());
            return "usuario-form-admin";
        }

        redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/admin/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(id, principal.getName());
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    private void cargarOpciones(Model model) {
        model.addAttribute("roles", Rol.values());
        model.addAttribute("practicas", TipoPractica.values());
    }
}
