package com.proyecto.service;

import java.util.List;

import com.proyecto.dto.LoginResponseDTO;
import com.proyecto.dto.RegistroUsuarioDTO;
import com.proyecto.dto.UsuarioResponseDTO;

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