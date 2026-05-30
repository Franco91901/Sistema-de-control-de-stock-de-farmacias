package com.proyecto.auth.application.mapper;

import com.proyecto.auth.application.dto.LoginResponseDTO;
import com.proyecto.auth.application.dto.RegistroUsuarioDTO;
import com.proyecto.auth.application.dto.UsuarioResponseDTO;
import com.proyecto.auth.domain.model.Rol;
import com.proyecto.core.sede.domain.model.Sede;
import com.proyecto.auth.domain.model.Usuario;

public class UsuarioMapperManual {

    public static UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioResponseDTO(
            usuario.getIdUsuario(),
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getTelefono(),
            usuario.getRol() != null ? usuario.getRol().getNombre() : null,
            usuario.getSede() != null ? usuario.getSede().getNombre() : null
        );
    }

    public static Usuario fromRegistroDTO(RegistroUsuarioDTO dto, Rol rol, Sede sede) {
        if (dto == null) return null;
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setEmail(dto.email());
        usuario.setTelefono(dto.telefono());
        usuario.setPassword(dto.password());
        usuario.setRol(rol);
        usuario.setSede(sede);
        return usuario;
    }

    public static LoginResponseDTO toLoginResponseDTO(Usuario usuario) {
        if (usuario == null) return null;
        return new LoginResponseDTO(
            usuario.getEmail(),
            usuario.getRol() != null ? usuario.getRol().getNombre() : null
        );
    }
}
