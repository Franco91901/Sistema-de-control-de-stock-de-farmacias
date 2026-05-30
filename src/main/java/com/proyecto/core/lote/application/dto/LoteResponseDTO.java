package com.proyecto.core.lote.application.dto;

import java.time.LocalDate;

public record LoteResponseDTO(
    Long idLote,
    String codigoLote,
    LocalDate fechaCaducidad,
    Integer stockLote,
    Long idMedicamento,
    String nombreMedicamento,
    String descripcionMedicamento,
    Long idSede,
    String nombreSede,
    Boolean proximoCaducar,
    Integer diasParaCaducar
) {}
