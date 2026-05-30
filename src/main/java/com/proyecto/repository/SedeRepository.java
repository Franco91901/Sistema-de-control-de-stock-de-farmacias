package com.proyecto.repository;

import com.proyecto.model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Long> {
    
    // Buscar sede por nombre
    Optional<Sede> findByNombre(String nombre);
    
    // Verificar si existe una sede con ese nombre
    boolean existsByNombre(String nombre);
}