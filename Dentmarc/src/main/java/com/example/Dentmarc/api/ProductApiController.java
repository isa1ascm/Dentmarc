package com.example.Dentmarc.api;

import com.example.Dentmarc.api.ApiDtos.ProductResponse;
import com.example.Dentmarc.models.Producto;
import com.example.Dentmarc.service.ProductoService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Validated
public class ProductApiController {
    private final ProductoService productoService;
    private final ApiMapper apiMapper;

    public ProductApiController(ProductoService productoService, ApiMapper apiMapper) {
        this.productoService = productoService;
        this.apiMapper = apiMapper;
    }

    @GetMapping
    public List<ProductResponse> list(@RequestParam(required = false) @Size(max = 100) String search,
                                      @RequestParam(required = false) @Positive Long categoryId) {
        return productoService.buscarProductos(search, categoryId, null).stream()
                .filter(producto -> producto.getCategoria().isActiva())
                .map(apiMapper::toProductResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable @Positive Long id) {
        Producto producto = productoService.obtenerProductoPorId(id);
        if (producto == null || !producto.getCategoria().isActiva()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No se encontró el producto solicitado.");
        }
        return apiMapper.toProductResponse(producto);
    }
}
