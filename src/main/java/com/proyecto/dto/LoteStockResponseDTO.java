package com.proyecto.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoteStockResponseDTO {
    
    private Long idLote;
    private String codigoLote;
    private LocalDate fechaCaducidad;
    private Integer stockLote;
    
    // Estados para la vista
    private String estadoCaducidad; // VIGENTE, PROXIMO, CADUCADO
    private Integer diasRestantes;
    
    // Info del medicamento
    private String nombreMedicamento;
    private Long idMedicamento;
}
