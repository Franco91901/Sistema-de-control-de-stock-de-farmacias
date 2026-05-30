package com.proyecto.auth.application.dto;

public record UsuarioResponseDTO(
    Integer id,
    String nombre,
    String email,
    String telefono,
    String rol,
    String sede
) {}
