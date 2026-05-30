package com.proyecto.mapper;

import com.proyecto.dto.OrdenTransportistaDTO;
import com.proyecto.model.DetalleOrden;

public class OrdenTransportistaMapper {

    public static OrdenTransportistaDTO toDTO(DetalleOrden d) {
        OrdenTransportistaDTO dto = new OrdenTransportistaDTO();
        dto.setIdDetalle(d.getIdDetalle());
        dto.setIdOrden(d.getOrden().getIdOrden());
        dto.setCantidad(d.getCantidad());
        dto.setEstado(d.getEstado());
        dto.setDireccionSede(d.getMedicamento().getSede().getDireccion());
        dto.setMedicamento(d.getMedicamento().getNombre());
        dto.setSede(d.getMedicamento().getSede().getNombre());

        return dto;
    }
}
