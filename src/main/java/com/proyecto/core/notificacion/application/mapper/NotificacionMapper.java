package com.proyecto.core.notificacion.application.mapper;

import com.proyecto.core.notificacion.application.dto.NotificacionDto;
import com.proyecto.core.notificacion.application.dto.NotificacionResponseDTO;
import com.proyecto.core.notificacion.domain.model.Notificacion;
import com.proyecto.core.medicamento.domain.model.Medicamento;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class NotificacionMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public NotificacionResponseDTO toResponseDTO(Notificacion n) {
        if (n == null) return null;
        Long idMedicamento = null;
        String nombreMedicamento = null;
        Integer stockMedicamento = null;
        if (n.getMedicamento() != null) {
            idMedicamento = n.getMedicamento().getIdMedicamento();
            nombreMedicamento = n.getMedicamento().getNombre();
            stockMedicamento = n.getMedicamento().getStockTotal();
        }
        Long idSede = null;
        String nombreSede = null;
        if (n.getSede() != null) {
            idSede = n.getSede().getIdSede();
            nombreSede = n.getSede().getNombre();
        }
        String fechaStr = n.getFecha() != null ? n.getFecha().toString() : null;
        String fechaFormateada = n.getFecha() != null ? n.getFecha().format(FORMATTER) : null;
        return new NotificacionResponseDTO(
            n.getIdNotificacion(),
            n.getMensaje(),
            n.getTipo(),
            n.getEstado(),
            fechaStr,
            fechaFormateada,
            idMedicamento,
            nombreMedicamento,
            stockMedicamento,
            idSede,
            nombreSede
        );
    }

    public Notificacion toEntityAuto(String mensaje, String tipo, Medicamento medicamento) {
        if (medicamento == null) return null;
        Notificacion notificacion = new Notificacion();
        notificacion.setMedicamento(medicamento);
        notificacion.setSede(medicamento.getSede());
        notificacion.setMensaje(mensaje);
        notificacion.setTipo(tipo);
        notificacion.setEstado("PENDIENTE");
        return notificacion;
    }

    public NotificacionDto toDto(Notificacion n) {
        if (n == null) return null;
        if (n.getIdNotificacion() != null && n.getIdNotificacion() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("ID de notificación demasiado grande para el reporte");
        }
        Integer id = n.getIdNotificacion() != null ? n.getIdNotificacion().intValue() : null;
        String nombreMedicamento = n.getMedicamento() != null ? n.getMedicamento().getNombre() : null;
        String nombreSede = n.getSede() != null ? n.getSede().getNombre() : null;
        return new NotificacionDto(id, n.getMensaje(), n.getFecha(), n.getEstado(), nombreMedicamento, nombreSede);
    }
}
