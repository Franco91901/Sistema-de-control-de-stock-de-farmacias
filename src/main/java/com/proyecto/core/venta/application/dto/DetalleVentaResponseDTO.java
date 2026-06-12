package com.proyecto.core.venta.application.dto;

import java.math.BigDecimal;

public record DetalleVentaResponseDTO(
    Long idDetalle,
    Long idMedicamento,
    String nombreMedicamento,
    Integer cantidad,
    BigDecimal precioUnitario,
    BigDecimal subtotal
) {}
