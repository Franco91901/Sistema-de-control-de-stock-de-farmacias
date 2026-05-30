package com.proyecto.core.notificacion.application.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record NotificacionResponseDTO(
    Long idNotificacion,
    String mensaje,
    String tipo,
    String estado,
    String fecha,
    String fechaFormateada,
    Long idMedicamento,
    String nombreMedicamento,
    Integer stockMedicamento,
    Long idSede,
    String nombreSede
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static NotificacionResponseDTO of(Long idNotificacion, String mensaje, String tipo,
                                             String estado, LocalDateTime fecha,
                                             String nombreMedicamento, String nombreSede) {
        return new NotificacionResponseDTO(
            idNotificacion, mensaje, tipo, estado,
            fecha.toString(),
            fecha.format(FORMATTER),
            null, nombreMedicamento, null, null, nombreSede
        );
    }
}
