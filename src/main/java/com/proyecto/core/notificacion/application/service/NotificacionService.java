package com.proyecto.core.notificacion.application.service;

import com.proyecto.core.lote.domain.model.Lote;
import com.proyecto.core.medicamento.domain.model.MedicamentoSede;
import com.proyecto.core.notificacion.application.dto.NotificacionResponseDTO;
import com.proyecto.core.notificacion.domain.model.TipoNotificacion;

import java.util.List;

public interface NotificacionService {

    List<NotificacionResponseDTO> listarNotificacionesPorSede(Long idSede);
    List<NotificacionResponseDTO> listarNotificacionesPendientes(Long idSede);
    List<NotificacionResponseDTO> listarNotificacionesPorTipo(Long idSede, TipoNotificacion tipo);
    NotificacionResponseDTO obtenerNotificacionPorId(Long idNotificacion);

    void verificarNotificacionBajoStock(MedicamentoSede medSede);
    void verificarNotificacionCaducidad(Lote lote);
    void generarNotificacionesAutomaticas(Long idSede);

    Long contarNotificacionesPendientes(Long idSede);
    Long contarNotificacionesPorTipo(Long idSede, TipoNotificacion tipo);
}
