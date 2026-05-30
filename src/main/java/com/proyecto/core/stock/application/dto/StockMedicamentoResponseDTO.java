package com.proyecto.core.stock.application.dto;

public record StockMedicamentoResponseDTO(
    Long idMedicamento,
    String nombreMedicamento,
    String descripcion,
    Integer stockTotal,
    Long idSede,
    String nombreSede,
    Integer cantidadLotes,
    Integer lotesProximosCaducar,
    String estadoStock,
    String claseCSS
) {}
