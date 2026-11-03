package com.example.Dentmarc.controllers;

import com.example.Dentmarc.models.Producto;
import com.example.Dentmarc.models.enums.EstadoProducto;
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
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/admin/productos")
    public String listarProductos(@RequestParam(name = "buscar", required = false) String buscar,
                                  @RequestParam(name = "categoria", required = false) Long categoria,
                                  @RequestParam(name = "estado", required = false) String estado,
                                  Model model) {
        model.addAttribute("productos", productoService.buscarProductos(buscar, categoria, estado));
        model.addAttribute("categorias", categoriaService.obtenerTodas());
        model.addAttribute("buscar", buscar);
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("totalProductos", productoService.contarTotal());
        model.addAttribute("activos", productoService.contarPorEstado(EstadoProducto.ACTIVO));
        model.addAttribute("stockBajo", productoService.contarPorEstado(EstadoProducto.STOCK_BAJO));
        model.addAttribute("agotados", productoService.contarPorEstado(EstadoProducto.AGOTADO));
        return "productos-admin";
    }

    @GetMapping("/admin/productos/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.obtenerTodas());
        return "producto-form-admin";
    }

    @PostMapping("/admin/productos/nuevo")
    public String guardarProducto(@RequestParam("nombre") String nombre,
                                  @RequestParam("marca") String marca,
                                  @RequestParam("sku") String sku,
                                  @RequestParam("precio") Double precio,
                                  @RequestParam("cantidad") int cantidad,
                                  @RequestParam(name = "idCategoria", required = false) Long idCategoria,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setMarca(marca);
        producto.setSKU(sku);
        producto.setPrecio(precio);
        producto.setCantidad(cantidad);

        try {
            productoService.guardarProducto(producto, idCategoria);
        } catch (IllegalArgumentException e) {
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categoriaService.obtenerTodas());
            model.addAttribute("idCategoriaSeleccionada", idCategoria);
            model.addAttribute("error", e.getMessage());
            return "producto-form-admin";
        }

        redirectAttributes.addFlashAttribute("mensaje", "Producto creado correctamente");
        return "redirect:/admin/productos";
    }

    @GetMapping("/admin/productos/editar/{id}")
    public String editarProducto(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Producto producto = productoService.obtenerProductoPorId(id);
        if (producto == null) {
            redirectAttributes.addFlashAttribute("error", "El producto no existe");
            return "redirect:/admin/productos";
        }

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.obtenerTodas());
        model.addAttribute("idCategoriaSeleccionada", producto.getCategoria().getId());
        return "producto-form-admin";
    }

    @PostMapping("/admin/productos/editar/{id}")
    public String actualizarProducto(@PathVariable("id") Long id,
                                     @RequestParam("nombre") String nombre,
                                     @RequestParam("marca") String marca,
                                     @RequestParam("sku") String sku,
                                     @RequestParam("precio") Double precio,
                                     @RequestParam("cantidad") int cantidad,
                                     @RequestParam(name = "idCategoria", required = false) Long idCategoria,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (productoService.obtenerProductoPorId(id) == null) {
            redirectAttributes.addFlashAttribute("error", "El producto no existe");
            return "redirect:/admin/productos";
        }

        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setMarca(marca);
        producto.setSKU(sku);
        producto.setPrecio(precio);
        producto.setCantidad(cantidad);

        try {
            productoService.guardarProducto(producto, idCategoria);
        } catch (IllegalArgumentException e) {
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categoriaService.obtenerTodas());
            model.addAttribute("idCategoriaSeleccionada", idCategoria);
            model.addAttribute("error", e.getMessage());
            return "producto-form-admin";
        }

        redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado correctamente");
        return "redirect:/admin/productos";
    }

    @PostMapping("/admin/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (productoService.obtenerProductoPorId(id) == null) {
            redirectAttributes.addFlashAttribute("error", "El producto no existe");
        } else {
            productoService.eliminarProducto(id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado correctamente");
        }
        return "redirect:/admin/productos";
    }
}
