package com.example.Dentmarc.repository;

import com.example.Dentmarc.models.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findTop5ByOrderByFechaDesc();

    Optional<Movimiento> findFirstByProducto_IdOrderByFechaDesc(Long idProducto);

    void deleteByProducto_Id(Long idProducto);
}
