package com.proyecto.core.stock.application.dto;

public record StockEstadisticasDTO(
    int totalMedicamentos,
    int totalStock,
    long medicamentosNormal,
    long medicamentosBajo,
    long medicamentosCritico,
    int lotesProximosCaducar
) {}
