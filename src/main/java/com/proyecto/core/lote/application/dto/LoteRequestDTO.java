package com.proyecto.core.lote.application.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record LoteRequestDTO(
    @NotNull(message = "El medicamento es obligatorio") Long idMedicamento,
    @NotNull(message = "La sede es obligatoria") Long idSede,
    @NotBlank(message = "El código de lote es obligatorio") String codigoLote,
    @NotNull(message = "La fecha de caducidad es obligatoria")
    @FutureOrPresent(message = "La fecha de caducidad debe ser hoy o futura")
    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaCaducidad,
    @NotNull(message = "El stock del lote es obligatorio")
    @Positive(message = "El stock debe ser mayor a cero") Integer stockLote
) {}
