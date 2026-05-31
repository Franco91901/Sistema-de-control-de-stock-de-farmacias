package com.proyecto.core.medicamento.domain.repository;

import com.proyecto.core.medicamento.domain.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    Optional<Medicamento> findByNombreIgnoreCase(String nombre);
}
