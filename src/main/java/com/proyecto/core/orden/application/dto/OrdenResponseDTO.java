package com.proyecto.core.orden.application.dto;

import java.time.LocalDateTime;

public record OrdenResponseDTO(
    Long idOrden,
    Long idUsuario,
    String nombreUsuario,
    Long idSede,
    String nombreSede,
    String tipo,
    String estado,
    Long idSedeDestino,
    String nombreSedeDestino,
    LocalDateTime fecha
) {}
