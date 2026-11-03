package com.example.Dentmarc.config;

import com.example.Dentmarc.models.Categoria;
import com.example.Dentmarc.models.Producto;
import com.example.Dentmarc.models.Usuario;
import com.example.Dentmarc.models.enums.Rol;
import com.example.Dentmarc.models.enums.TipoMovimiento;
import com.example.Dentmarc.models.enums.TipoPractica;
import com.example.Dentmarc.repository.CategoriaRepository;
import com.example.Dentmarc.repository.ProductoRepository;
import com.example.Dentmarc.repository.UsuarioRepository;
import com.example.Dentmarc.service.CategoriaService;
import com.example.Dentmarc.service.InventarioService;
import com.example.Dentmarc.service.ProductoService;
import com.example.Dentmarc.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Datos mock iniciales (administrador, clientes, categorias y productos); solo se insertan si las tablas estan vacias
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private InventarioService inventarioService;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            cargarUsuarios();
        }
        if (categoriaRepository.count() == 0) {
            cargarCategorias();
        }
        if (productoRepository.count() == 0) {
            cargarProductos();
        }
    }

    private void cargarUsuarios() {
        // Administrador: admin@dentmarc.mx / Admin123!
        crearUsuario("Ana", "Martínez", "admin@dentmarc.mx", "5590003276", TipoPractica.CLINICA, Rol.ADMINISTRADOR, "Admin123!");
        // Clientes de ejemplo: Cliente123!
        crearUsuario("Mariana", "López García", "mariana@clinicadental.mx", "5512345678", TipoPractica.CLINICA, Rol.USUARIO, "Cliente123!");
        crearUsuario("Javier", "Montes", "javier@consultorio.mx", "5511223344", TipoPractica.CONSULTORIO, Rol.USUARIO, "Cliente123!");
        crearUsuario("Sofía", "Ramírez", "sofia@labdental.mx", "5599887766", TipoPractica.LABORATORIO, Rol.USUARIO, "Cliente123!");
    }

    private void crearUsuario(String nombre, String apellidos, String email, String telefono, TipoPractica tipoPractica, Rol rol, String contrasenia) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setTipoPractica(tipoPractica);
        usuario.setRol(rol);
        usuarioService.crearUsuario(usuario, contrasenia);
    }

    private void cargarCategorias() {
        crearCategoria("Restauración", "fa-wand-magic-sparkles");
        crearCategoria("Consumibles", "fa-syringe");
        crearCategoria("Instrumental", "fa-stethoscope");
        crearCategoria("Equipamiento", "fa-desktop");
        crearCategoria("Ortodoncia", "fa-teeth-open");
        crearCategoria("Endodoncia", "fa-heart-pulse");
    }

    private void crearCategoria(String nombre, String icono) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setIcono(icono);
        categoria.setActiva(true);
        categoriaService.guardarCategoria(categoria);
    }

    private void cargarProductos() {
        crearProducto("Resina universal Filtek Z350 XT", "3M", "RES-3M-0350", "Restauración", 1289.0, 84, 0);
        crearProducto("Adhesivo Single Bond Universal", "3M", "ADH-3M-0051", "Restauración", 1045.0, 67, 0);
        crearProducto("Guantes nitrilo azul · 100 pzas", "Ambiderm", "GUA-AMB-0100", "Consumibles", 249.0, 30, 12);
        crearProducto("Kit aislamiento absoluto", "Nic Tone", "KIT-NIC-0312", "Consumibles", 845.0, 10, 10);
        crearProducto("Anestesia tópica cereza", "Zeyco", "AN-ZEY-004B", "Consumibles", 189.0, 17, 3);
        crearProducto("Pieza alta velocidad CX207-C", "Coxo", "PIE-COX-0207", "Instrumental", 2490.0, 32, 0);
        crearProducto("Lámpara fotocurado Curing Pen", "Woodpecker", "LAM-WOD-0148", "Equipamiento", 2890.0, 11, 2);
        crearProducto("Escáner intraoral Aoralscan 3", "Shining 3D", "ESC-S3D-0003", "Equipamiento", 98900.0, 4, 0);
        crearProducto("Brackets metálicos Roth 0.022", "Dentmarc", "ORT-DEN-0022", "Ortodoncia", 780.0, 45, 0);
        crearProducto("Limas rotatorias NiTi 25 mm", "Dentmarc", "END-DEN-0025", "Endodoncia", 1150.0, 22, 0);
    }

    // Se crea con stock 0 y se registra una ENTRADA (y una SALIDA opcional) para que quede historial de movimientos
    private void crearProducto(String nombre, String marca, String sku, String nombreCategoria, Double precio, int entrada, int salida) {
        Categoria categoria = categoriaRepository.findByNombreIgnoreCase(nombreCategoria).orElse(null);

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setMarca(marca);
        producto.setSKU(sku);
        producto.setPrecio(precio);
        producto.setCantidad(0);
        productoService.guardarProducto(producto, categoria.getId());

        inventarioService.registrarMovimiento(producto.getId(), TipoMovimiento.ENTRADA, entrada);
        if (salida > 0) {
            inventarioService.registrarMovimiento(producto.getId(), TipoMovimiento.SALIDA, salida);
        }
    }
}
