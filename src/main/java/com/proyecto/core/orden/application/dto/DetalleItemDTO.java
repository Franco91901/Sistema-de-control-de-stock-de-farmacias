package com.proyecto.core.orden.application.dto;

public record DetalleItemDTO(
    String medicamento,
    Integer cantidad,
    String estado
) {}
