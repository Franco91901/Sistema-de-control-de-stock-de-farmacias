package com.proyecto.core.notificacion.application.service;

import com.proyecto.core.notificacion.application.dto.NotificacionResponseDTO;
import java.util.List;

public interface NotificacionService {
    
    // Consultas (solo lectura para farmacéutico)
    List<NotificacionResponseDTO> listarNotificacionesPorSede(Long idSede);
    List<NotificacionResponseDTO> listarNotificacionesPendientes(Long idSede);
    List<NotificacionResponseDTO> listarNotificacionesPorTipo(Long idSede, String tipo);
    NotificacionResponseDTO obtenerNotificacionPorId(Long idNotificacion);
    
    // Notificaciones automáticas
    void verificarNotificacionBajoStock(com.proyecto.core.medicamento.domain.model.Medicamento medicamento);
    void verificarNotificacionCaducidad(com.proyecto.core.lote.domain.model.Lote lote);
    void generarNotificacionesAutomaticas(Long idSede);
    
    // Estadísticas
    Long contarNotificacionesPendientes(Long idSede);
    Long contarNotificacionesPorTipo(Long idSede, String tipo);
}