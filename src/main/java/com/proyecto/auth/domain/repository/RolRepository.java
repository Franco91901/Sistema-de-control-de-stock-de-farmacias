package com.proyecto.auth.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.auth.domain.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    // Buscar rol por nombre (útil para asignar rol por defecto)
    Optional<Rol> findByNombre(String nombre);
}