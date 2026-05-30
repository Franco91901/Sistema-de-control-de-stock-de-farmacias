package com.proyecto.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    List<Usuario> findByRol_IdRol(Integer idRol);

    List<Usuario> findByRol_Nombre(String nombreRol);

    List<Usuario> findBySede_IdSede(Long idSede);

    List<Usuario> findBySede_Nombre(String nombreSede);
}
