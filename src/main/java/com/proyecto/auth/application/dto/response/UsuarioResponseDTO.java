package com.proyecto.auth.application.dto.response;

public record UsuarioResponseDTO(
    Long id,
    String nombre,
    String apellido,
    String email,
    String telefono,
    String dni,
    Boolean activo,
    String rol,
    Long idSede,
    String sede
) {}
