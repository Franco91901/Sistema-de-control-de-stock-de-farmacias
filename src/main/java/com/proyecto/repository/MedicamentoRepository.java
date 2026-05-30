package com.proyecto.repository;


import com.proyecto.model.Medicamento;
import com.proyecto.model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    
    // Buscar medicamentos por sede
    List<Medicamento> findBySedeIdSede(Long idSede);
    
    // Buscar medicamento por nombre y sede (para validar duplicados)
    Optional<Medicamento> findBySedeAndNombre(Sede sede, String nombre);
    
    // Buscar medicamento por nombre ignorando mayúsculas/minúsculas en una sede
    @Query("SELECT m FROM Medicamento m WHERE LOWER(m.nombre) = LOWER(:nombre) AND m.sede.idSede = :idSede")
    Optional<Medicamento> findByNombreIgnoreCaseAndSede(@Param("nombre") String nombre, @Param("idSede") Long idSede);
    
    // Contar medicamentos por sede
    Long countBySedeIdSede(Long idSede);
    
    // Buscar medicamentos con bajo stock (ej: menos de 10 unidades)
    @Query("SELECT m FROM Medicamento m WHERE m.sede.idSede = :idSede AND m.stockTotal < :umbral")
    List<Medicamento> findMedicamentosBajoStock(@Param("idSede") Long idSede, @Param("umbral") Integer umbral);
}