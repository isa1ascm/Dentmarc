package com.example.Dentmarc.controllers;

import com.example.Dentmarc.models.Categoria;
import com.example.Dentmarc.models.enums.EstadoProducto;
import com.example.Dentmarc.models.enums.Rol;
import com.example.Dentmarc.service.CategoriaService;
import com.example.Dentmarc.service.InventarioService;
import com.example.Dentmarc.service.ProductoService;
import com.example.Dentmarc.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ResumenController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/admin/resumen")
    public String dashboard(Model model) {
        List<Categoria> categorias = categoriaService.obtenerTodas();
        int maximo = 0;
        for (Categoria categoria : categorias) {
            maximo = Math.max(maximo, categoria.getProductos().size());
        }

        model.addAttribute("hoy", LocalDate.now());
        model.addAttribute("totalProductos", productoService.contarTotal());
        model.addAttribute("totalCategorias", categorias.size());
        model.addAttribute("unidades", productoService.sumarUnidades());
        model.addAttribute("valorInventario", productoService.calcularValorInventario());
        model.addAttribute("conAlerta", productoService.contarConAlerta());
        model.addAttribute("agotados", productoService.contarPorEstado(EstadoProducto.AGOTADO));
        model.addAttribute("totalClientes", usuarioService.contarPorRol(Rol.USUARIO));
        model.addAttribute("categorias", categorias);
        model.addAttribute("maximoCategoria", maximo);
        model.addAttribute("alertas", productoService.obtenerAlertas());
        model.addAttribute("clientes", usuarioService.obtenerClientesRecientes());
        model.addAttribute("movimientos", inventarioService.obtenerMovimientosRecientes());
        return "resumen";
    }
}
