package com.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionResponseDTO {
    
    private Long idNotificacion;
    private String mensaje;
    private String tipo; 
    private String estado;
    private String fecha;
    private String fechaFormateada;
    private Long idMedicamento;
    private String nombreMedicamento;
    private Integer stockMedicamento;
    private Long idSede;
    private String nombreSede;
    
    // Constructor simplificado
    public NotificacionResponseDTO(Long idNotificacion, String mensaje, String tipo, 
                                  String estado, LocalDateTime fecha, 
                                  String nombreMedicamento, String nombreSede) {
        this.idNotificacion = idNotificacion;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.estado = estado;
        setFecha(fecha);
        this.nombreMedicamento = nombreMedicamento;
        this.nombreSede = nombreSede;
    }
    
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha.toString();
        this.fechaFormateada = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
