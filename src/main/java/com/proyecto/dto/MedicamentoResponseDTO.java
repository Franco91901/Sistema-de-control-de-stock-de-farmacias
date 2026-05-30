package com.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicamentoResponseDTO {
    
    private Long idMedicamento;
    private String nombre;
    private String descripcion;
    private Integer stockTotal;
    private Long idSede;
    private String nombreSede;
    private String direccionSede;
    private Integer cantidadLotes;
    private Boolean bajoStock; // true si stock < 10
}