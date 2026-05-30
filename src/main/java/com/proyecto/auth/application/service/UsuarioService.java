package com.proyecto.auth.application.service;

import java.util.List;

import com.proyecto.auth.application.dto.LoginResponseDTO;
import com.proyecto.auth.application.dto.RegistroUsuarioDTO;
import com.proyecto.auth.application.dto.UsuarioResponseDTO;

public interface UsuarioService {

	 // ================= REGISTRO =================
    UsuarioResponseDTO crearUsuario(RegistroUsuarioDTO dto);

    // ================= LISTAR =================
    List<UsuarioResponseDTO> listarUsuarios();

    // ================= OBTENER =================
    UsuarioResponseDTO obtenerPorEmail(String email);

    // ================= ADMIN =================
    UsuarioResponseDTO buscarPorEmail(String email);

    UsuarioResponseDTO actualizarUsuario(String email, RegistroUsuarioDTO dto);

    void eliminarUsuario(String email);

    // ================= LOGIN =================
    LoginResponseDTO login(String email, String password);

    // ================= PASSWORD =================
    void enviarTokenRecuperacion(String email);

    void resetPassword(String token, String nuevaPassword);
}