package com.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockMedicamentoResponseDTO {
    
    private Long idMedicamento;
    private String nombreMedicamento;
    private String descripcion;
    private Integer stockTotal;
    private Long idSede;
    private String nombreSede;
    private Integer cantidadLotes;
    private Integer lotesProximosCaducar; // lotes que caducan en ≤ 30 días
    
    // Para mostrar colores/iconos en la vista
    private String estadoStock; // NORMAL, BAJO, CRITICO
    private String claseCSS; // Para Bootstrap: success, warning, danger
}