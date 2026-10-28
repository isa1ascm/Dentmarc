package com.example.Dentmarc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaController {

	@GetMapping({ "/", "/inicio" })
	public String inicio() {
		return "inicio";
	}

	@GetMapping("/carrito")
	public String carrito() {
		return "carrito";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/login-admin")
	public String loginAdmin() {
		return "login-admin";
	}

	@GetMapping("/registro")
	public String registro() {
		return "registro";
	}

	@GetMapping("/categorias")
	public String categorias() {
		return "categorias";
	}

	@GetMapping("/productos")
	public String productos() {
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

	@GetMapping("/cuenta")
	public String cuenta() {
		return "cuenta";
	}

	@GetMapping("/admin/resumen")
	public String resumen() {
		return "resumen";
	}

	@GetMapping("/admin/inventario")
	public String inventario() {
		return "inventario";
	}

	@GetMapping("/admin/productos")
	public String productosAdmin() {
		return "productos-admin";
	}

	@GetMapping("/admin/categorias")
	public String categoriasAdmin() {
		return "categorias-admin";
	}

}
