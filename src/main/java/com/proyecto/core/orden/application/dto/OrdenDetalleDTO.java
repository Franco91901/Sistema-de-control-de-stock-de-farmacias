package com.proyecto.core.orden.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrdenDetalleDTO(
    Long idOrden,
    String gestor,
    String tipo,
    String estado,
    LocalDateTime fecha,
    String sede,
    List<DetalleItemDTO> detalles
) {}
