package com.proyecto.auth.application.service;

import com.proyecto.auth.application.dto.response.UsuarioResponseDTO;
import com.proyecto.auth.application.dto.request.RegisterRequestDTO;

import java.util.List;

public interface UsuarioService {

    UsuarioResponseDTO crearUsuario(RegisterRequestDTO dto);
    List<UsuarioResponseDTO> listarUsuarios();
    UsuarioResponseDTO obtenerPorEmail(String email);
    UsuarioResponseDTO buscarPorEmail(String email);
    UsuarioResponseDTO actualizarUsuario(String email, RegisterRequestDTO dto);
    void eliminarUsuario(String email);
    UsuarioResponseDTO aprobarUsuario(String email);
    List<UsuarioResponseDTO> listarPendientes();
}
