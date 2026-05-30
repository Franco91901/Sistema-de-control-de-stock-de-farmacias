package com.proyecto.mapper;

import com.proyecto.dto.LoteRequestDTO;
import com.proyecto.dto.LoteResponseDTO;
import com.proyecto.dto.LoteStockResponseDTO;
import com.proyecto.model.Lote;
import com.proyecto.model.Medicamento;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class LoteMapper {
    
    // Convertir RequestDTO a Entity
    public Lote toEntity(LoteRequestDTO requestDTO, Medicamento medicamento) {
        if (requestDTO == null) return null;
        
        Lote lote = new Lote();
        lote.setMedicamento(medicamento);
        lote.setCodigoLote(requestDTO.getCodigoLote());
        lote.setFechaCaducidad(requestDTO.getFechaCaducidad());
        lote.setStockLote(requestDTO.getStockLote());
        
        return lote;
    }
    
    // Convertir Entity a ResponseDTO (con información completa)
    public LoteResponseDTO toResponseDTO(Lote lote) {
        if (lote == null) return null;
        
        LoteResponseDTO responseDTO = new LoteResponseDTO();
        responseDTO.setIdLote(lote.getIdLote());
        responseDTO.setCodigoLote(lote.getCodigoLote());
        responseDTO.setFechaCaducidad(lote.getFechaCaducidad());
        responseDTO.setStockLote(lote.getStockLote());
        
        if (lote.getMedicamento() != null) {
            responseDTO.setIdMedicamento(lote.getMedicamento().getIdMedicamento());
            responseDTO.setNombreMedicamento(lote.getMedicamento().getNombre());
            responseDTO.setDescripcionMedicamento(lote.getMedicamento().getDescripcion());
            
            if (lote.getMedicamento().getSede() != null) {
                responseDTO.setIdSede(lote.getMedicamento().getSede().getIdSede());
                responseDTO.setNombreSede(lote.getMedicamento().getSede().getNombre());
            }
        }
        
        // Calcular si está próximo a caducar (≤ 30 días)
        LocalDate hoy = LocalDate.now();
        long diasParaCaducar = ChronoUnit.DAYS.between(hoy, lote.getFechaCaducidad());
        responseDTO.setDiasParaCaducar((int) diasParaCaducar);
        responseDTO.setProximoCaducar(diasParaCaducar <= 30);
        
        return responseDTO;
    }
    
    // Convertir Entity a LoteStockResponseDTO (para vista de stock)
    public LoteStockResponseDTO toStockResponseDTO(Lote lote) {
        if (lote == null) return null;
        
        LoteStockResponseDTO stockDTO = new LoteStockResponseDTO();
        stockDTO.setIdLote(lote.getIdLote());
        stockDTO.setCodigoLote(lote.getCodigoLote());
        stockDTO.setFechaCaducidad(lote.getFechaCaducidad());
        stockDTO.setStockLote(lote.getStockLote());
        
        if (lote.getMedicamento() != null) {
            stockDTO.setIdMedicamento(lote.getMedicamento().getIdMedicamento());
            stockDTO.setNombreMedicamento(lote.getMedicamento().getNombre());
        }
        
        // Determinar estado de caducidad
        LocalDate hoy = LocalDate.now();
        long diasRestantes = ChronoUnit.DAYS.between(hoy, lote.getFechaCaducidad());
        stockDTO.setDiasRestantes((int) diasRestantes);
        
        if (diasRestantes < 0) {
            stockDTO.setEstadoCaducidad("CADUCADO");
        } else if (diasRestantes <= 30) {
            stockDTO.setEstadoCaducidad("PROXIMO");
        } else {
            stockDTO.setEstadoCaducidad("VIGENTE");
        }
        
        return stockDTO;
    }
    
    // Actualizar Entity con datos de RequestDTO
    public void updateEntity(Lote lote, LoteRequestDTO requestDTO) {
        if (lote == null || requestDTO == null) return;
        
        lote.setCodigoLote(requestDTO.getCodigoLote());
        lote.setFechaCaducidad(requestDTO.getFechaCaducidad());
        lote.setStockLote(requestDTO.getStockLote());
        // El medicamento NO se actualiza (es fijo)
    }
}