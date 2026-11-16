package com.example.Dentmarc.api;

import com.example.Dentmarc.models.enums.EstadoProducto;
import com.example.Dentmarc.models.enums.TipoPractica;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record LoginRequest(
            @NotBlank @Email @Size(max = 160) String email,
            @NotBlank String contrasenia) {
    }

    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 80) String nombre,
            @NotBlank @Size(max = 100) String apellidos,
            @NotBlank @Email @Size(max = 160) String email,
            @NotBlank @Pattern(regexp = "[0-9 ]{10,14}") String telefono,
            @NotNull TipoPractica tipoPractica,
            @NotBlank @Pattern(regexp = "(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}",
                    message = "Debe incluir 8 caracteres, una mayúscula, un número y un carácter especial")
            String contrasenia) {
    }

    public record UserResponse(Long id, String nombre, String apellidos, String email,
                               String telefono, TipoPractica tipoPractica) {
    }

    public record AuthResponse(String token, String tipo, long expiraEnSegundos, UserResponse usuario) {
    }

    public record CategoryResponse(Long id, String nombre, String icono, long cantidadProductos) {
    }

    public record ProductResponse(Long id, String nombre, String marca, String sku, double precio,
                                  int cantidadDisponible, EstadoProducto estado, CategorySummary categoria) {
    }

    public record CategorySummary(Long id, String nombre) {
    }

    public record CartItemRequest(@NotNull @Positive Long productId, @NotNull @Positive Integer cantidad) {
    }

    public record CartQuantityRequest(@NotNull @Positive Integer cantidad) {
    }

    public record CartItemResponse(ProductResponse producto, int cantidad, double precioUnitario, double total) {
    }

    public record CartResponse(List<CartItemResponse> items, int cantidadProductos, double subtotal) {
    }

    public record FieldError(String campo, String mensaje) {
    }

    public record ErrorResponse(String timestamp, int status, String error, String message,
                                String path, List<FieldError> fieldErrors) {
    }
}
