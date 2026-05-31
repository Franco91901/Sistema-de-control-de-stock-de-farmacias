package com.proyecto.auth.application.dto.response;

public record AuthResponseDTO(
    UsuarioResponseDTO usuario,
    String token
) {}
