package com.proyecto.mapper;

import com.proyecto.dto.LoginResponseDTO;
import com.proyecto.dto.RegistroUsuarioDTO;
import com.proyecto.dto.UsuarioResponseDTO;
import com.proyecto.model.Rol;
import com.proyecto.model.Sede;
import com.proyecto.model.Usuario;

public class UsuarioMapperManual {

    // -------------------------------
    // Usuario -> UsuarioResponseDTO
    // -------------------------------
    public static UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        if (usuario == null) return null;

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setTelefono(usuario.getTelefono());
        dto.setRol(usuario.getRol() != null ? usuario.getRol().getNombre() : null);
        dto.setSede(usuario.getSede() != null ? usuario.getSede().getNombre() : null);

        return dto;
    }

    // -------------------------------
    // RegistroUsuarioDTO -> Usuario
    // -------------------------------
    public static Usuario fromRegistroDTO(RegistroUsuarioDTO dto, Rol rol, Sede sede) {
        if (dto == null) return null;

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setPassword(dto.getPassword());
        usuario.setRol(rol);   // Debes pasar el objeto Rol existente
        usuario.setSede(sede); // Debes pasar el objeto Sede existente

        return usuario;
    }

    // -------------------------------
    // Usuario -> LoginResponseDTO
    // -------------------------------
    public static LoginResponseDTO toLoginResponseDTO(Usuario usuario) {
        if (usuario == null) return null;

        String rolNombre = usuario.getRol() != null ? usuario.getRol().getNombre() : null;

        return new LoginResponseDTO(usuario.getEmail(), rolNombre);
    }
}
