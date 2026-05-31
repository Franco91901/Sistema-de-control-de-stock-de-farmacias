package com.proyecto.core.sede.application.mapper;

import com.proyecto.core.sede.application.dto.SedeDTO;
import com.proyecto.core.sede.domain.model.Sede;
import org.springframework.stereotype.Component;

@Component
public class SedeMapper {

    public SedeDTO toDTO(Sede sede) {
        if (sede == null) return null;
        return new SedeDTO(sede.getIdSede(), sede.getNombre(), sede.getDireccion());
    }

    public Sede toEntity(SedeDTO dto) {
        if (dto == null) return null;
        Sede sede = new Sede();
        sede.setIdSede(dto.idSede());
        sede.setNombre(dto.nombre());
        sede.setDireccion(dto.direccion());
        return sede;
    }
}
