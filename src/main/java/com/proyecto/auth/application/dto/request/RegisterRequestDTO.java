package com.proyecto.auth.application.dto.request;

import jakarta.validation.constraints.*;

public record RegisterRequestDTO(

        @NotBlank
        String nombre,

        String apellido,

        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 8)
        String password,

        @Pattern(regexp = "9\\d{8}", message = "El teléfono debe tener 9 dígitos y empezar con 9")
        String telefono,

        @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
        String dni,

        Integer rolId,

        Long sedeId
) {}
