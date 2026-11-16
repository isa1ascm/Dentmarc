package com.example.Dentmarc.repository;

import com.example.Dentmarc.models.ApiCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiCartItemRepository extends JpaRepository<ApiCartItem, Long> {
    List<ApiCartItem> findAllByUsuario_IdOrderByIdAsc(Long usuarioId);

    Optional<ApiCartItem> findByUsuario_IdAndProducto_Id(Long usuarioId, Long productoId);
}
