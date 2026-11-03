package com.example.Dentmarc.controllers;

import com.example.Dentmarc.models.Producto;
import com.example.Dentmarc.models.Usuario;
import com.example.Dentmarc.models.enums.Rol;
import com.example.Dentmarc.models.enums.TipoPractica;
import com.example.Dentmarc.service.ProductoService;
import com.example.Dentmarc.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TiendaController {

    private static final String USUARIO_SESION = "usuarioId";
    private static final String CARRITO_SESION = "carrito";

    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public TiendaController(ProductoService productoService, UsuarioService usuarioService) {
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam(name = "txt_busqueda", required = false) String busqueda) {
        return "redirect:/productos?buscar=" + (busqueda == null ? "" : java.net.URLEncoder.encode(busqueda.trim(), java.nio.charset.StandardCharsets.UTF_8));
    }

    @PostMapping("/autenticacion")
    public String autenticar(@RequestParam("correo") String correo,
                             @RequestParam("contrasenia") String contrasenia,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.autenticarUsuario(correo.trim().toLowerCase(), contrasenia);
        if (usuario == null || usuario.getRol() != Rol.USUARIO) {
            redirectAttributes.addFlashAttribute("error", "Correo o contraseña incorrectos.");
            return "redirect:/login";
        }
        session.setAttribute(USUARIO_SESION, usuario.getId());
        return "redirect:/cuenta";
    }

    @PostMapping("/crear-cuenta")
    public String crearCuenta(@RequestParam("nombre") String nombre,
                              @RequestParam("apellidos") String apellidos,
                              @RequestParam("correo") String correo,
                              @RequestParam(name = "tel_telefono", required = false) String telefono,
                              @RequestParam("sel_practica") String practica,
                              @RequestParam("contrasenia") String contrasenia,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setEmail(correo);
        usuario.setTelefono(telefono == null ? "" : telefono);
        usuario.setTipoPractica(TipoPractica.valueOf(practica.toUpperCase()));
        usuario.setRol(Rol.USUARIO);

        try {
            usuarioService.crearUsuario(usuario, contrasenia);
            session.setAttribute(USUARIO_SESION, usuario.getId());
            redirectAttributes.addFlashAttribute("mensaje", "Cuenta creada correctamente.");
            return "redirect:/cuenta";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/registro";
        }
    }

    @PostMapping("/cerrar-sesion")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/inicio";
    }

    @PostMapping("/suscribir")
    public String suscribir(@RequestParam("correo") String correo, RedirectAttributes redirectAttributes) {
        if (correo == null || correo.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Escribe un correo válido.");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Gracias. Te enviaremos nuestras novedades.");
        }
        return "redirect:/inicio";
    }

    @PostMapping("/carrito/agregar/{id}")
    public String agregarAlCarrito(@PathVariable Long id,
                                   @RequestParam(name = "cantidad", defaultValue = "1") int cantidad,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Producto producto = productoService.obtenerProductoPorId(id);
        if (producto == null || producto.getCantidad() <= 0) {
            redirectAttributes.addFlashAttribute("error", "El producto no está disponible.");
            return "redirect:/productos";
        }
        if (cantidad < 1 || cantidad > producto.getCantidad()) {
            redirectAttributes.addFlashAttribute("error", "La cantidad solicitada no está disponible.");
            return "redirect:/productos";
        }
        Map<Long, Integer> carrito = obtenerCarrito(session);
        int nuevaCantidad = carrito.getOrDefault(id, 0) + cantidad;
        if (nuevaCantidad > producto.getCantidad()) {
            redirectAttributes.addFlashAttribute("error", "No hay suficiente stock para agregar esa cantidad.");
        } else {
            carrito.put(id, nuevaCantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Producto agregado al carrito.");
        }
        session.setAttribute(CARRITO_SESION, carrito);
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/actualizar")
    public String actualizarCarrito(@RequestParam("idProducto") Long idProducto,
                                    @RequestParam("cantidad") int cantidad,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Producto producto = productoService.obtenerProductoPorId(idProducto);
        Map<Long, Integer> carrito = obtenerCarrito(session);
        if (producto == null || cantidad < 1 || cantidad > producto.getCantidad()) {
            redirectAttributes.addFlashAttribute("error", "La cantidad solicitada no está disponible.");
        } else {
            carrito.put(idProducto, cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Carrito actualizado.");
        }
        session.setAttribute(CARRITO_SESION, carrito);
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/eliminar/{id}")
    public String eliminarDelCarrito(@PathVariable Long id, HttpSession session) {
        Map<Long, Integer> carrito = obtenerCarrito(session);
        carrito.remove(id);
        session.setAttribute(CARRITO_SESION, carrito);
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/cupon")
    public String aplicarCupon(@RequestParam("txt_cupon") String cupon, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("mensaje", cupon == null || cupon.isBlank()
                ? "Escribe un cupón para aplicarlo."
                : "El cupón se validará al confirmar tu pedido.");
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/envio")
    public String calcularEnvio(@RequestParam("txt_cp") String codigoPostal, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("mensaje", codigoPostal != null && codigoPostal.matches("\\d{5}")
                ? "Envío estándar disponible para tu código postal."
                : "Escribe un código postal válido de cinco dígitos.");
        return "redirect:/carrito";
    }

    @GetMapping("/carrito")
    public String mostrarCarrito(HttpSession session, Model model) {
        Map<Long, Integer> cantidades = obtenerCarrito(session);
        List<Map<String, Object>> items = new ArrayList<>();
        double subtotal = 0;
        for (Map.Entry<Long, Integer> entry : cantidades.entrySet()) {
            Producto producto = productoService.obtenerProductoPorId(entry.getKey());
            if (producto == null) {
                continue;
            }
            int cantidad = Math.min(entry.getValue(), producto.getCantidad());
            if (cantidad <= 0) {
                continue;
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("producto", producto);
            item.put("cantidad", cantidad);
            item.put("total", producto.getPrecio() * cantidad);
            items.add(item);
            subtotal += producto.getPrecio() * cantidad;
        }
        model.addAttribute("items", items);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("totalProductos", cantidades.values().stream().mapToInt(Integer::intValue).sum());
        return "carrito";
    }

    @GetMapping("/cuenta")
    public String mostrarCuenta(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioActual(session);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia sesión para ver tu cuenta.");
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        return "cuenta";
    }

    private Usuario usuarioActual(HttpSession session) {
        Object id = session.getAttribute(USUARIO_SESION);
        return id instanceof Long ? usuarioService.obtenerUsuarioPorId((Long) id) : null;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> obtenerCarrito(HttpSession session) {
        Object carrito = session.getAttribute(CARRITO_SESION);
        if (carrito instanceof Map<?, ?>) {
            return (Map<Long, Integer>) carrito;
        }
        Map<Long, Integer> nuevoCarrito = new HashMap<>();
        session.setAttribute(CARRITO_SESION, nuevoCarrito);
        return nuevoCarrito;
    }
}
