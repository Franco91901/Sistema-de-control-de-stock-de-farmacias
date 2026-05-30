package com.proyecto.mapper;


import com.proyecto.dto.MedicamentoRequestDTO;
import com.proyecto.dto.MedicamentoResponseDTO;
import com.proyecto.model.Medicamento;
import com.proyecto.model.Sede;
import org.springframework.stereotype.Component;

@Component
public class MedicamentoMapper {
    
    // Convertir RequestDTO a Entity (para crear)
    public Medicamento toEntity(MedicamentoRequestDTO requestDTO, Sede sede) {
        if (requestDTO == null) return null;
        
        Medicamento medicamento = new Medicamento();
        medicamento.setNombre(requestDTO.getNombre());
        medicamento.setDescripcion(requestDTO.getDescripcion());
        medicamento.setSede(sede);
        medicamento.setStockTotal(0); // Inicia con 0 stock
        
        return medicamento;
    }
    
    // Convertir Entity a ResponseDTO (para mostrar)
    public MedicamentoResponseDTO toResponseDTO(Medicamento medicamento) {
        if (medicamento == null) return null;
        
        MedicamentoResponseDTO responseDTO = new MedicamentoResponseDTO();
        responseDTO.setIdMedicamento(medicamento.getIdMedicamento());
        responseDTO.setNombre(medicamento.getNombre());
        responseDTO.setDescripcion(medicamento.getDescripcion());
        responseDTO.setStockTotal(medicamento.getStockTotal());
        
        if (medicamento.getSede() != null) {
            responseDTO.setIdSede(medicamento.getSede().getIdSede());
            responseDTO.setNombreSede(medicamento.getSede().getNombre());
            responseDTO.setDireccionSede(medicamento.getSede().getDireccion());
        }
        
        // Calcular si está bajo stock (menos de 10 unidades)
        responseDTO.setBajoStock(medicamento.getStockTotal() < 10);
        
        return responseDTO;
    }
    
    // Actualizar Entity con datos de RequestDTO
    public void updateEntity(Medicamento medicamento, MedicamentoRequestDTO requestDTO) {
        if (medicamento == null || requestDTO == null) return;
        
        medicamento.setNombre(requestDTO.getNombre());
        medicamento.setDescripcion(requestDTO.getDescripcion());
        // La sede NO se actualiza (es fija para el farmacéutico)
    }
}
