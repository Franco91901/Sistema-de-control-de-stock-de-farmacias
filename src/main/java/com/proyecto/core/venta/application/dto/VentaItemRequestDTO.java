package com.proyecto.core.venta.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record VentaItemRequestDTO(
    @NotNull(message = "El medicamento es obligatorio")
    Long idMedicamento,

    @NotNull(message = "La cantidad es obligatoria") @Positive(message = "La cantidad debe ser mayor a cero")
    Integer cantidad,

    @NotNull(message = "El precio es obligatorio") @PositiveOrZero(message = "El precio no puede ser negativo")
    BigDecimal precioUnitario
) {}
