package com.proyecto.core.venta.application.dto;

import java.math.BigDecimal;

public record MedicamentoDisponibleDTO(
    Long idMedicamento,
    String nombre,
    String descripcion,
    Integer stockDisponible,
    BigDecimal precio
) {}
