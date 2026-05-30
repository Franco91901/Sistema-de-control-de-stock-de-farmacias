package com.proyecto.core.orden.application.mapper;

import com.proyecto.core.orden.application.dto.OrdenTransportistaDTO;
import com.proyecto.core.orden.domain.model.DetalleOrden;

public class OrdenTransportistaMapper {

    public static OrdenTransportistaDTO toDTO(DetalleOrden d) {
        return new OrdenTransportistaDTO(
            d.getIdDetalle(),
            d.getOrden().getIdOrden(),
            d.getMedicamento().getNombre(),
            d.getMedicamento().getSede().getNombre(),
            d.getMedicamento().getSede().getDireccion(),
            d.getCantidad(),
            d.getEstado()
        );
    }
}
