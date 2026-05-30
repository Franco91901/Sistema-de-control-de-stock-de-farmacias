package com.proyecto.core.orden.application.dto;

public record OrdenTransportistaDTO(
    Long idDetalle,
    Long idOrden,
    String medicamento,
    String sede,
    String direccionSede,
    Integer cantidad,
    String estado
) {}
