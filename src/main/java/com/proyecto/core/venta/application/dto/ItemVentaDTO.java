package com.proyecto.core.venta.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemVentaDTO(
    @NotNull(message = "El ID del medicamento es obligatorio")
    Long idMedicamento,
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    Integer cantidad
) {}
