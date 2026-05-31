package com.proyecto.core.notificacion.application.dto;

public record NotificacionEstadisticasDTO(
    Long pendientes,
    Long bajoStock,
    Long proximoCaducar
) {}
