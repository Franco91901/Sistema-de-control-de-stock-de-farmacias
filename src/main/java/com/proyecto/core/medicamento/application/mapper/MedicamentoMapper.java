package com.proyecto.core.medicamento.application.mapper;

import com.proyecto.core.medicamento.application.dto.MedicamentoRequestDTO;
import com.proyecto.core.medicamento.application.dto.MedicamentoResponseDTO;
import com.proyecto.core.medicamento.domain.model.Medicamento;
import com.proyecto.core.sede.domain.model.Sede;
import org.springframework.stereotype.Component;

@Component
public class MedicamentoMapper {

    public Medicamento toEntity(MedicamentoRequestDTO dto, Sede sede) {
        if (dto == null) return null;
        Medicamento medicamento = new Medicamento();
        medicamento.setNombre(dto.nombre());
        medicamento.setDescripcion(dto.descripcion());
        medicamento.setSede(sede);
        medicamento.setStockTotal(0);
        return medicamento;
    }

    public MedicamentoResponseDTO toResponseDTO(Medicamento medicamento) {
        if (medicamento == null) return null;
        Long idSede = null;
        String nombreSede = null;
        String direccionSede = null;
        if (medicamento.getSede() != null) {
            idSede = medicamento.getSede().getIdSede();
            nombreSede = medicamento.getSede().getNombre();
            direccionSede = medicamento.getSede().getDireccion();
        }
        return new MedicamentoResponseDTO(
            medicamento.getIdMedicamento(),
            medicamento.getNombre(),
            medicamento.getDescripcion(),
            medicamento.getStockTotal(),
            idSede,
            nombreSede,
            direccionSede,
            null,
            medicamento.getStockTotal() < 10
        );
    }

    public void updateEntity(Medicamento medicamento, MedicamentoRequestDTO dto) {
        if (medicamento == null || dto == null) return;
        medicamento.setNombre(dto.nombre());
        medicamento.setDescripcion(dto.descripcion());
    }
}
