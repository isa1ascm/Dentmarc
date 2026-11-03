package com.example.Dentmarc.controllers;

import com.example.Dentmarc.service.ProductoService;
import com.example.Dentmarc.service.CategoriaService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaginaController {

	private final ProductoService productoService;
	private final CategoriaService categoriaService;

	public PaginaController(ProductoService productoService, CategoriaService categoriaService) {
		this.productoService = productoService;
		this.categoriaService = categoriaService;
	}

	@GetMapping({ "/", "/inicio" })
	public String inicio() {
		return "inicio";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/registro")
	public String registro() {
		return "registro";
	}

	@GetMapping("/categorias")
	public String categorias(@RequestParam(name = "categoria", required = false) Long idCategoria, Model model) {
		model.addAttribute("categorias", categoriaService.obtenerTodas());
		model.addAttribute("productos", productoService.buscarProductos(null, idCategoria, null));
		model.addAttribute("categoriaSeleccionada", idCategoria);
		return "categorias";
	}

	@GetMapping("/productos")
	public String productos(@RequestParam(name = "buscar", required = false) String buscar, Model model) {
		model.addAttribute("productos", productoService.buscarProductos(buscar, null, null));
		model.addAttribute("buscar", buscar);
		return "productos";
	}

	@GetMapping("/mas-vendidos")
	public String masVendidos() {
		return "masVendidos";
	}

	@GetMapping("/ofertas")
	public String ofertas() {
		return "ofertas";
	}

}
