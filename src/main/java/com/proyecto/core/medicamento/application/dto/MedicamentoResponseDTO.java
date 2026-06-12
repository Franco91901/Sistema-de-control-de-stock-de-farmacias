package com.proyecto.core.medicamento.application.dto;

import java.math.BigDecimal;

public record MedicamentoResponseDTO(
    Long idMedicamento,
    String nombre,
    String descripcion,
    BigDecimal precioVenta,
    Integer stockTotal,
    Long idSede,
    String nombreSede,
    String direccionSede,
    Integer cantidadLotes,
    Boolean bajoStock
) {}
