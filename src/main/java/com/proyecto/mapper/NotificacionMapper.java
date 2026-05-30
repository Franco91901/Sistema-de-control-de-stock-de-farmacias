package com.proyecto.mapper;


import com.proyecto.dto.NotificacionDto;
import com.proyecto.dto.NotificacionResponseDTO;
import com.proyecto.model.Notificacion;
import org.springframework.stereotype.Component;

@Component
public class NotificacionMapper {
    
    // Convertir Entity a ResponseDTO (solo lectura para farmacéutico)
    public NotificacionResponseDTO toResponseDTO(Notificacion notificacion) {
        if (notificacion == null) return null;
        
        NotificacionResponseDTO responseDTO = new NotificacionResponseDTO();
        responseDTO.setIdNotificacion(notificacion.getIdNotificacion());
        responseDTO.setMensaje(notificacion.getMensaje());
        responseDTO.setTipo(notificacion.getTipo());
        responseDTO.setEstado(notificacion.getEstado());
        responseDTO.setFecha(notificacion.getFecha());
        
        if (notificacion.getMedicamento() != null) {
            responseDTO.setIdMedicamento(notificacion.getMedicamento().getIdMedicamento());
            responseDTO.setNombreMedicamento(notificacion.getMedicamento().getNombre());
            responseDTO.setStockMedicamento(notificacion.getMedicamento().getStockTotal());
        }
        
        if (notificacion.getSede() != null) {
            responseDTO.setIdSede(notificacion.getSede().getIdSede());
            responseDTO.setNombreSede(notificacion.getSede().getNombre());
        }
        
        return responseDTO;
    }
    
    // Método para crear notificación automática
    public Notificacion toEntityAuto(String mensaje, String tipo, com.proyecto.model.Medicamento medicamento) {
        if (medicamento == null) return null;
        
        Notificacion notificacion = new Notificacion();
        notificacion.setMedicamento(medicamento);
        notificacion.setSede(medicamento.getSede());
        notificacion.setMensaje(mensaje);
        // No se guarda en BD pero se mantiene en memoria
        notificacion.setTipo(tipo);
        notificacion.setEstado("PENDIENTE");
        
        return notificacion;
    }
    
 // En NotificacionMapper.java
    public NotificacionDto toDto(Notificacion notificacion) {
        if (notificacion == null) return null;

        NotificacionDto dto = new NotificacionDto();
        
        if (notificacion.getIdNotificacion() != null) {
            if (notificacion.getIdNotificacion() > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("ID de notificación demasiado grande para el reporte");
            }
            dto.setIdNotificacion(notificacion.getIdNotificacion().intValue());
        }

        dto.setMensaje(notificacion.getMensaje());
        dto.setFecha(notificacion.getFecha());
        dto.setEstado(notificacion.getEstado());
        
        if (notificacion.getMedicamento() != null) {
            dto.setNombreMedicamento(notificacion.getMedicamento().getNombre());
        }
        
        if (notificacion.getSede() != null) {
            dto.setNombreSede(notificacion.getSede().getNombre());
        }
        
        return dto;
    }
}