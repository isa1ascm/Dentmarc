package com.example.Dentmarc.controllers;

import com.example.Dentmarc.models.Producto;
import com.example.Dentmarc.models.enums.EstadoProducto;
import com.example.Dentmarc.models.enums.TipoMovimiento;
import com.example.Dentmarc.service.InventarioService;
import com.example.Dentmarc.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private ProductoService productoService;

    @GetMapping("/admin/inventario")
    public String listarInventario(@RequestParam(name = "buscar", required = false) String buscar,
                                   @RequestParam(name = "estado", required = false) String estado,
                                   Model model) {
        List<Producto> productos = productoService.buscarProductos(buscar, null, estado);

        model.addAttribute("productos", productos);
        model.addAttribute("todosLosProductos", productoService.obtenerTodos());
        model.addAttribute("ultimosMovimientos", inventarioService.obtenerUltimosMovimientos(productos));
        model.addAttribute("buscar", buscar);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("unidades", productoService.sumarUnidades());
        model.addAttribute("activos", productoService.contarPorEstado(EstadoProducto.ACTIVO));
        model.addAttribute("stockBajo", productoService.contarPorEstado(EstadoProducto.STOCK_BAJO));
        model.addAttribute("agotados", productoService.contarPorEstado(EstadoProducto.AGOTADO));
        return "inventario";
    }

    @PostMapping("/admin/inventario/movimiento")
    public String registrarMovimiento(@RequestParam("idProducto") Long idProducto,
                                      @RequestParam("tipo") TipoMovimiento tipo,
                                      @RequestParam("cantidad") int cantidad,
                                      RedirectAttributes redirectAttributes) {
        try {
            inventarioService.registrarMovimiento(idProducto, tipo, cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Movimiento registrado correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/inventario";
    }
}
