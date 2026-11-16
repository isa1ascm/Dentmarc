package com.example.Dentmarc.api;

import com.example.Dentmarc.api.ApiDtos.CategoryResponse;
import com.example.Dentmarc.api.ApiDtos.CategorySummary;
import com.example.Dentmarc.api.ApiDtos.ProductResponse;
import com.example.Dentmarc.api.ApiDtos.UserResponse;
import com.example.Dentmarc.models.Categoria;
import com.example.Dentmarc.models.Producto;
import com.example.Dentmarc.models.Usuario;
import com.example.Dentmarc.repository.ProductoRepository;
import org.springframework.stereotype.Component;

@Component
public class ApiMapper {
    private final ProductoRepository productoRepository;

    public ApiMapper(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public UserResponse toUserResponse(Usuario usuario) {
        return new UserResponse(usuario.getId(), usuario.getNombre(), usuario.getApellidos(),
                usuario.getEmail(), usuario.getTelefono(), usuario.getTipoPractica());
    }

    public ProductResponse toProductResponse(Producto producto) {
        Categoria categoria = producto.getCategoria();
        return new ProductResponse(producto.getId(), producto.getNombre(), producto.getMarca(),
                producto.getSKU(), producto.getPrecio(), producto.getCantidad(),
                producto.getEstadoProducto(), new CategorySummary(categoria.getId(), categoria.getNombre()));
    }

    public CategoryResponse toCategoryResponse(Categoria categoria) {
        return new CategoryResponse(categoria.getId(), categoria.getNombre(), categoria.getIcono(),
                productoRepository.countByCategoria_Id(categoria.getId()));
    }
}
