package com.example.Dentmarc.controllers;

import com.example.Dentmarc.models.Categoria;
import com.example.Dentmarc.service.CategoriaService;
import com.example.Dentmarc.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @GetMapping("/admin/categorias")
    public String listarCategorias(@RequestParam(name = "buscar", required = false) String buscar,
                                   @RequestParam(name = "estado", required = false) String estado,
                                   Model model) {
        model.addAttribute("categorias", categoriaService.buscarCategorias(buscar, estado));
        model.addAttribute("buscar", buscar);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("activas", categoriaService.contarActivas());
        model.addAttribute("ocultas", categoriaService.obtenerOcultas());
        model.addAttribute("totalProductos", productoService.contarTotal());
        model.addAttribute("conAlerta", productoService.contarConAlerta());
        return "categorias-admin";
    }

    @GetMapping("/admin/categorias/nueva")
    public String mostrarFormulario(Model model) {
        Categoria categoria = new Categoria();
        categoria.setActiva(true);
        categoria.setIcono("fa-tag");
        model.addAttribute("categoria", categoria);
        return "categoria-form-admin";
    }

    @PostMapping("/admin/categorias/nueva")
    public String guardarCategoria(@RequestParam("nombre") String nombre,
                                   @RequestParam("icono") String icono,
                                   @RequestParam(name = "activa", required = false) boolean activa,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setIcono(icono);
        categoria.setActiva(activa);

        try {
            categoriaService.guardarCategoria(categoria);
        } catch (IllegalArgumentException e) {
            model.addAttribute("categoria", categoria);
            model.addAttribute("error", e.getMessage());
            return "categoria-form-admin";
        }

        redirectAttributes.addFlashAttribute("mensaje", "Categoría creada correctamente");
        return "redirect:/admin/categorias";
    }

    @GetMapping("/admin/categorias/editar/{id}")
    public String editarCategoria(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Categoria categoria = categoriaService.obtenerCategoriaPorId(id);
        if (categoria == null) {
            redirectAttributes.addFlashAttribute("error", "La categoría no existe");
            return "redirect:/admin/categorias";
        }

        model.addAttribute("categoria", categoria);
        return "categoria-form-admin";
    }

    @PostMapping("/admin/categorias/editar/{id}")
    public String actualizarCategoria(@PathVariable("id") Long id,
                                      @RequestParam("nombre") String nombre,
                                      @RequestParam("icono") String icono,
                                      @RequestParam(name = "activa", required = false) boolean activa,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (categoriaService.obtenerCategoriaPorId(id) == null) {
            redirectAttributes.addFlashAttribute("error", "La categoría no existe");
            return "redirect:/admin/categorias";
        }

        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNombre(nombre);
        categoria.setIcono(icono);
        categoria.setActiva(activa);

        try {
            categoriaService.guardarCategoria(categoria);
        } catch (IllegalArgumentException e) {
            model.addAttribute("categoria", categoria);
            model.addAttribute("error", e.getMessage());
            return "categoria-form-admin";
        }

        redirectAttributes.addFlashAttribute("mensaje", "Categoría actualizada correctamente");
        return "redirect:/admin/categorias";
    }

    @PostMapping("/admin/categorias/eliminar/{id}")
    public String eliminarCategoria(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoriaService.eliminarCategoria(id);
            redirectAttributes.addFlashAttribute("mensaje", "Categoría eliminada correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categorias";
    }
}
