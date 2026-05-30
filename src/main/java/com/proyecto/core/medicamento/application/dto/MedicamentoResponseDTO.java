package com.proyecto.core.medicamento.application.dto;

public record MedicamentoResponseDTO(
    Long idMedicamento,
    String nombre,
    String descripcion,
    Integer stockTotal,
    Long idSede,
    String nombreSede,
    String direccionSede,
    Integer cantidadLotes,
    Boolean bajoStock
) {}
