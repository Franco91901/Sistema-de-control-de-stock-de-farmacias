package com.proyecto.auth.application.dto.response;

public record AdminEstadisticasDTO(
    long usuariosActivos,
    long totalUsuarios,
    long totalSedes,
    long ordenesPendientes,
    long totalOrdenes
) {}
