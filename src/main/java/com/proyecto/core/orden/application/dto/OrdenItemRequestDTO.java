package com.proyecto.core.orden.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrdenItemRequestDTO(
    @NotNull(message = "El medicamento es obligatorio") Long idMedicamento,
    @NotNull(message = "La cantidad es obligatoria") @Positive(message = "La cantidad debe ser mayor a 0") Integer cantidad
) {}
