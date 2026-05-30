package com.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionVistaDTO {
    private Long idNotificacion;
    private String mensaje;
    private String fechaFormateada;
    private String estado;
    private String nombreMedicamento;
    private String nombreSede;
}
