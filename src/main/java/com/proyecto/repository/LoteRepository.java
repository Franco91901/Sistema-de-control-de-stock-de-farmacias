package com.proyecto.repository;

import com.proyecto.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    
    // Buscar lotes por medicamento
    List<Lote> findByMedicamentoIdMedicamento(Long idMedicamento);
    
    // Buscar lotes por sede (a través del medicamento)
    @Query("SELECT l FROM Lote l WHERE l.medicamento.sede.idSede = :idSede")
    List<Lote> findBySedeId(@Param("idSede") Long idSede);
    
    // Buscar lotes próximos a caducar (ej: en los próximos 30 días)
    @Query("SELECT l FROM Lote l WHERE l.medicamento.sede.idSede = :idSede AND l.fechaCaducidad BETWEEN :hoy AND :fechaLimite")
    List<Lote> findLotesProximosCaducar(
        @Param("idSede") Long idSede,
        @Param("hoy") LocalDate hoy,
        @Param("fechaLimite") LocalDate fechaLimite
    );
    
    // Sumar stock total de todos los lotes de un medicamento
    @Query("SELECT COALESCE(SUM(l.stockLote), 0) FROM Lote l WHERE l.medicamento.idMedicamento = :idMedicamento")
    Integer sumStockByMedicamento(@Param("idMedicamento") Long idMedicamento);
    
    // Buscar lote por código
    Optional<Lote> findByCodigoLote(String codigoLote);
    
    // Eliminar lotes por medicamento
    void deleteByMedicamentoIdMedicamento(Long idMedicamento);
}