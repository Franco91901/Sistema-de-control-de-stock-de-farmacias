package com.proyecto.core.venta.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponseDTO(
    Long idVenta,
    LocalDateTime fecha,
    BigDecimal total,
    String nombreSede,
    String nombreUsuario,
    List<DetalleVentaResponseDTO> detalles
) {}
