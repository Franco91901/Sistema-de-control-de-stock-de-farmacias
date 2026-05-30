package com.proyecto.service;

import com.proyecto.dto.NotificacionResponseDTO;
import java.util.List;

public interface NotificacionService {
    
    // Consultas (solo lectura para farmacéutico)
    List<NotificacionResponseDTO> listarNotificacionesPorSede(Long idSede);
    List<NotificacionResponseDTO> listarNotificacionesPendientes(Long idSede);
    List<NotificacionResponseDTO> listarNotificacionesPorTipo(Long idSede, String tipo);
    NotificacionResponseDTO obtenerNotificacionPorId(Long idNotificacion);
    
    // Notificaciones automáticas
    void verificarNotificacionBajoStock(com.proyecto.model.Medicamento medicamento);
    void verificarNotificacionCaducidad(com.proyecto.model.Lote lote);
    void generarNotificacionesAutomaticas(Long idSede);
    
    // Estadísticas
    Long contarNotificacionesPendientes(Long idSede);
    Long contarNotificacionesPorTipo(Long idSede, String tipo);
}