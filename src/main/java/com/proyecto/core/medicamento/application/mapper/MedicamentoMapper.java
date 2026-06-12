package com.proyecto.core.medicamento.application.mapper;

import com.proyecto.core.medicamento.application.dto.MedicamentoRequestDTO;
import com.proyecto.core.medicamento.application.dto.MedicamentoResponseDTO;
import com.proyecto.core.medicamento.domain.model.Medicamento;
import com.proyecto.core.medicamento.domain.model.MedicamentoSede;
import org.springframework.stereotype.Component;

@Component
public class MedicamentoMapper {

    public Medicamento toEntity(MedicamentoRequestDTO dto) {
        if (dto == null) return null;
        Medicamento medicamento = new Medicamento();
        medicamento.setNombre(dto.nombre());
        medicamento.setDescripcion(dto.descripcion());
        if (dto.precioVenta() != null) medicamento.setPrecioVenta(dto.precioVenta());
        return medicamento;
    }

    public MedicamentoResponseDTO toResponseDTO(MedicamentoSede medSede) {
        if (medSede == null) return null;
        Medicamento medicamento = medSede.getMedicamento();
        Long idSede = null;
        String nombreSede = null;
        String direccionSede = null;
        if (medSede.getSede() != null) {
            idSede = medSede.getSede().getIdSede();
            nombreSede = medSede.getSede().getNombre();
            direccionSede = medSede.getSede().getDireccion();
        }
        return new MedicamentoResponseDTO(
            medicamento.getIdMedicamento(),
            medicamento.getNombre(),
            medicamento.getDescripcion(),
            medicamento.getPrecioVenta(),
            medSede.getStockTotal(),
            idSede,
            nombreSede,
            direccionSede,
            null,
            medSede.getStockTotal() < 10
        );
    }

    public void updateEntity(Medicamento medicamento, MedicamentoRequestDTO dto) {
        if (medicamento == null || dto == null) return;
        medicamento.setNombre(dto.nombre());
        medicamento.setDescripcion(dto.descripcion());
        if (dto.precioVenta() != null) medicamento.setPrecioVenta(dto.precioVenta());
    }
}
