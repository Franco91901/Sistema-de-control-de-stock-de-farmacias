package com.proyecto.mapper;

import com.proyecto.dto.SedeDTO;
import com.proyecto.model.Sede;
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
        sede.setIdSede(dto.getIdSede());
        sede.setNombre(dto.getNombre());
        sede.setDireccion(dto.getDireccion());
        return sede;
    }
}
