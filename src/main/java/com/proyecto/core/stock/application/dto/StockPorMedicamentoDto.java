package com.proyecto.core.stock.application.dto;

public record StockPorMedicamentoDto(
    Long idMedicamento,
    String nombre,
    String nombreSede,
    Integer stockTotal
) {}
