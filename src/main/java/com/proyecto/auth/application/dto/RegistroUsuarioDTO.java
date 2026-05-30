package com.proyecto.auth.application.dto;

import jakarta.validation.constraints.NotBlank;

public record RegistroUsuarioDTO(
    @NotBlank String nombre,
    @NotBlank String email,
    @NotBlank String telefono,
    @NotBlank String password,
    Integer rolId,
    Long sedeId
) {}
