package com.proyecto.core.stock.application.dto;

public record StockPorMedicamentoDto(
    Integer idMedicamento,
    String nombre,
    String nombreSede,
    Integer stockTotal
) {}
