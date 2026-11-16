package com.example.Dentmarc.service;

import com.example.Dentmarc.api.ApiDtos.CartItemResponse;
import com.example.Dentmarc.api.ApiDtos.CartResponse;
import com.example.Dentmarc.api.ApiDtos.ProductResponse;
import com.example.Dentmarc.api.ApiException;
import com.example.Dentmarc.api.ApiMapper;
import com.example.Dentmarc.models.ApiCartItem;
import com.example.Dentmarc.models.Producto;
import com.example.Dentmarc.models.Usuario;
import com.example.Dentmarc.repository.ApiCartItemRepository;
import com.example.Dentmarc.repository.ProductoRepository;
import com.example.Dentmarc.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApiCartService {
    private final ApiCartItemRepository cartItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ApiMapper apiMapper;

    public ApiCartService(ApiCartItemRepository cartItemRepository, UsuarioRepository usuarioRepository,
                          ProductoRepository productoRepository, ApiMapper apiMapper) {
        this.cartItemRepository = cartItemRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.apiMapper = apiMapper;
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(String email) {
        Usuario usuario = findUser(email);
        return toCartResponse(usuario);
    }

    @Transactional
    public CartResponse addItem(String email, Long productId, int cantidad) {
        Usuario usuario = findUser(email);
        Producto producto = findAvailableProduct(productId);
        ApiCartItem item = cartItemRepository.findByUsuario_IdAndProducto_Id(usuario.getId(), productId)
                .orElseGet(() -> {
                    ApiCartItem newItem = new ApiCartItem();
                    newItem.setUsuario(usuario);
                    newItem.setProducto(producto);
                    newItem.setCantidad(0);
                    return newItem;
                });
        int nuevaCantidad = item.getCantidad() + cantidad;
        validateStock(producto, nuevaCantidad);
        item.setCantidad(nuevaCantidad);
        cartItemRepository.save(item);
        return toCartResponse(usuario);
    }

    @Transactional
    public CartResponse updateItem(String email, Long productId, int cantidad) {
        Usuario usuario = findUser(email);
        ApiCartItem item = cartItemRepository.findByUsuario_IdAndProducto_Id(usuario.getId(), productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El producto no está en el carrito."));
        validateStock(item.getProducto(), cantidad);
        item.setCantidad(cantidad);
        cartItemRepository.save(item);
        return toCartResponse(usuario);
    }

    @Transactional
    public CartResponse removeItem(String email, Long productId) {
        Usuario usuario = findUser(email);
        ApiCartItem item = cartItemRepository.findByUsuario_IdAndProducto_Id(usuario.getId(), productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El producto no está en el carrito."));
        cartItemRepository.delete(item);
        return toCartResponse(usuario);
    }

    private Usuario findUser(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No se encontró la cuenta."));
    }

    private Producto findAvailableProduct(Long productId) {
        Producto producto = productoRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No se encontró el producto solicitado."));
        if (!producto.getCategoria().isActiva()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No se encontró el producto solicitado.");
        }
        return producto;
    }

    private void validateStock(Producto producto, int cantidad) {
        if (cantidad < 1 || cantidad > producto.getCantidad()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La cantidad solicitada no está disponible.");
        }
    }

    private CartResponse toCartResponse(Usuario usuario) {
        List<CartItemResponse> items = cartItemRepository.findAllByUsuario_IdOrderByIdAsc(usuario.getId())
                .stream()
                .map(item -> {
                    ProductResponse product = apiMapper.toProductResponse(item.getProducto());
                    double total = item.getProducto().getPrecio() * item.getCantidad();
                    return new CartItemResponse(product, item.getCantidad(),
                            item.getProducto().getPrecio(), total);
                })
                .toList();
        int cantidadProductos = items.stream().mapToInt(CartItemResponse::cantidad).sum();
        double subtotal = items.stream().mapToDouble(CartItemResponse::total).sum();
        return new CartResponse(items, cantidadProductos, subtotal);
    }
}
