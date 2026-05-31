package com.proyecto.core.stock.domain.repository;

import com.proyecto.core.stock.domain.model.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {

    List<MovimientoStock> findBySedeIdSedeOrderByFechaDesc(Long idSede);

    List<MovimientoStock> findByMedicamentoIdMedicamentoAndSedeIdSedeOrderByFechaDesc(Long idMedicamento, Long idSede);

    @Query("SELECT m FROM MovimientoStock m WHERE m.sede.idSede = :idSede AND m.tipo = :tipo ORDER BY m.fecha DESC")
    List<MovimientoStock> findBySedeAndTipo(@Param("idSede") Long idSede, @Param("tipo") String tipo);
}
