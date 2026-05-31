package com.proyecto.core.orden.application.mapper;

import com.proyecto.core.orden.application.dto.OrdenTransportistaDTO;
import com.proyecto.core.orden.domain.model.DetalleOrden;

public class OrdenTransportistaMapper {

    public static OrdenTransportistaDTO toDTO(DetalleOrden d) {
        String nombreSede = null;
        String direccionSede = null;
        if (d.getOrden() != null && d.getOrden().getSede() != null) {
            nombreSede = d.getOrden().getSede().getNombre();
            direccionSede = d.getOrden().getSede().getDireccion();
        }
        return new OrdenTransportistaDTO(
            d.getIdDetalle(),
            d.getOrden().getIdOrden(),
            d.getMedicamento().getNombre(),
            nombreSede,
            direccionSede,
            d.getCantidad(),
            d.getEstado() != null ? d.getEstado().name() : null
        );
    }
}
