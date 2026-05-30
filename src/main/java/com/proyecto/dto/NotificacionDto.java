package com.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionDto {
    private Integer idNotificacion;
    private String mensaje;
    private LocalDateTime fecha;
    private String estado;
    private String nombreMedicamento;
    private String nombreSede;
}