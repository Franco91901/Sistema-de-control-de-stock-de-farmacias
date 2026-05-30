package com.proyecto.core.notificacion.application.dto;

public record NotificacionVistaDTO(
    Long idNotificacion,
    String mensaje,
    String fechaFormateada,
    String estado,
    String nombreMedicamento,
    String nombreSede
) {}
