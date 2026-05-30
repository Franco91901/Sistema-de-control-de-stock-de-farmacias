package com.proyecto.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoteResponseDTO {
    
    private Long idLote;
    private String codigoLote;
    private LocalDate fechaCaducidad;
    private Integer stockLote;
    private Long idMedicamento;
    private String nombreMedicamento;
    private String descripcionMedicamento;
    private Long idSede;
    private String nombreSede;
    private Boolean proximoCaducar; // true si caduca en ≤ 30 días
    private Integer diasParaCaducar;
}