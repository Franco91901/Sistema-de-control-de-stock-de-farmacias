package com.proyecto.core.orden.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrdenRequestDTO(
    @NotNull(message = "La sede es obligatoria") Long idSede,
    Long idSedeDestino,
    @NotNull(message = "El tipo de orden es obligatorio") String tipo,
    @NotEmpty(message = "La orden debe tener al menos un ítem") List<@Valid OrdenItemRequestDTO> items
) {}
