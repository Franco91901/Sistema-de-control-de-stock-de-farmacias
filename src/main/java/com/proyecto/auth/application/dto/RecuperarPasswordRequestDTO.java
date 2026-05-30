package com.proyecto.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecuperarPasswordRequestDTO(
    @Email @NotBlank String email
) {}
