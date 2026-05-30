package com.proyecto.core.notificacion.application.dto;

import java.time.LocalDateTime;

public record NotificacionDto(
    Integer idNotificacion,
    String mensaje,
    LocalDateTime fecha,
    String estado,
    String nombreMedicamento,
    String nombreSede
) {}
