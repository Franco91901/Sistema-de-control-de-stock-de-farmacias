package com.proyecto.core.stock.application.dto;

public record StockPorSedeDto(
    Long idSede,
    String nombre,
    Integer stockTotal
) {}
