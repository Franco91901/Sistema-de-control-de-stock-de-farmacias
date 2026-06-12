package com.proyecto.core.medicamento.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MedicamentoRequestDTO(

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres") String nombre,
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres") String descripcion,
    @PositiveOrZero(message = "El precio no puede ser negativo") BigDecimal precioVenta,
    Long idSede
) {}
