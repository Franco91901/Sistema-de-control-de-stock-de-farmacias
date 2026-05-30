package com.proyecto.core.stock.application.dto;

public record StockPorSedeDto(
    Integer idSede,
    String nombre,
    Integer stockTotal
) {}
