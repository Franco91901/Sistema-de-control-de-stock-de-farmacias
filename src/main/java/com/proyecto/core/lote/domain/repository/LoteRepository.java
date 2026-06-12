package com.proyecto.core.lote.domain.repository;

import com.proyecto.core.lote.domain.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    List<Lote> findByMedicamentoIdMedicamento(Long idMedicamento);

    List<Lote> findBySedeIdSede(Long idSede);

    @Query("SELECT l FROM Lote l WHERE l.sede.idSede = :idSede")
    List<Lote> findBySedeId(@Param("idSede") Long idSede);

    @Query("SELECT l FROM Lote l WHERE l.sede.idSede = :idSede AND l.fechaCaducidad BETWEEN :hoy AND :fechaLimite")
    List<Lote> findLotesProximosCaducar(
        @Param("idSede") Long idSede,
        @Param("hoy") LocalDate hoy,
        @Param("fechaLimite") LocalDate fechaLimite
    );

    @Query("SELECT COALESCE(SUM(l.stockLote), 0) FROM Lote l WHERE l.medicamento.idMedicamento = :idMedicamento AND l.sede.idSede = :idSede")
    Integer sumStockByMedicamentoAndSede(@Param("idMedicamento") Long idMedicamento, @Param("idSede") Long idSede);

    @Query("SELECT COALESCE(SUM(l.stockLote), 0) FROM Lote l WHERE l.medicamento.idMedicamento = :idMedicamento")
    Integer sumStockByMedicamento(@Param("idMedicamento") Long idMedicamento);

    Optional<Lote> findByCodigoLote(String codigoLote);

    void deleteByMedicamentoIdMedicamento(Long idMedicamento);

    @Query("SELECT l FROM Lote l WHERE l.medicamento.idMedicamento = :idMedicamento AND l.sede.idSede = :idSede AND l.stockLote > 0 ORDER BY l.fechaCaducidad ASC")
    List<Lote> findLotesDisponiblesFIFO(@Param("idMedicamento") Long idMedicamento, @Param("idSede") Long idSede);
}
