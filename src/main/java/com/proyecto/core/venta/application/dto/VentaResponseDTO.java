package com.proyecto.core.venta.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponseDTO(
    Long idVenta,
    Long idUsuario,
    String nombreUsuario,
    Long idSede,
    String nombreSede,
    LocalDateTime fecha,
    BigDecimal total,
    Integer cantidadItems,
    List<DetalleVentaResponseDTO> detalles
) {}
