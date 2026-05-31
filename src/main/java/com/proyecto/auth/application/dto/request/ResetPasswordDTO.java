package com.proyecto.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordDTO(
    @NotBlank String token,
    @NotBlank String nuevaPassword
) {}
