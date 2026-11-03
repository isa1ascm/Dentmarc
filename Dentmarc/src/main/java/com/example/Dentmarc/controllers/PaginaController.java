package com.example.Dentmarc.controllers;

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

}
