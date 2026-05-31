package com.proyecto.core.medicamento.domain.repository;

import com.proyecto.core.medicamento.domain.model.MedicamentoSede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoSedeRepository extends JpaRepository<MedicamentoSede, Long> {

    List<MedicamentoSede> findBySedeIdSede(Long idSede);

    List<MedicamentoSede> findByMedicamentoIdMedicamento(Long idMedicamento);

    Optional<MedicamentoSede> findByMedicamentoIdMedicamentoAndSedeIdSede(Long idMedicamento, Long idSede);

    Long countBySedeIdSede(Long idSede);

    @Query("SELECT ms FROM MedicamentoSede ms WHERE LOWER(ms.medicamento.nombre) = LOWER(:nombre) AND ms.sede.idSede = :idSede")
    Optional<MedicamentoSede> findByNombreIgnoreCaseAndSede(@Param("nombre") String nombre, @Param("idSede") Long idSede);

    @Query("SELECT ms FROM MedicamentoSede ms WHERE ms.sede.idSede = :idSede AND ms.stockTotal < :umbral")
    List<MedicamentoSede> findMedicamentosBajoStock(@Param("idSede") Long idSede, @Param("umbral") Integer umbral);
}
