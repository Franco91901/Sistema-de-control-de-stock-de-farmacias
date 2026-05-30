package com.proyecto.core.lote.application.dto;

import java.time.LocalDate;

public record LoteStockResponseDTO(
    Long idLote,
    String codigoLote,
    LocalDate fechaCaducidad,
    Integer stockLote,
    String estadoCaducidad,
    Integer diasRestantes,
    String nombreMedicamento,
    Long idMedicamento
) {}
