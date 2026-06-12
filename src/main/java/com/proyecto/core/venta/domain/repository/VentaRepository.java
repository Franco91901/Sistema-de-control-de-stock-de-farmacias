package com.proyecto.core.venta.domain.repository;

import com.proyecto.core.venta.domain.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findBySedeIdSedeOrderByFechaDesc(Long idSede);
}
