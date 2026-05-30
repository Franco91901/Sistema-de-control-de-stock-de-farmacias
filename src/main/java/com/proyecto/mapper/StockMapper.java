package com.proyecto.mapper;

import com.proyecto.dto.StockMedicamentoResponseDTO;
import com.proyecto.model.Medicamento;
import com.proyecto.model.Lote;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class StockMapper {
    
    // Convertir Medicamento + lotes a StockMedicamentoResponseDTO
    public StockMedicamentoResponseDTO toStockResponseDTO(Medicamento medicamento, List<Lote> lotes) {
        if (medicamento == null) return null;
        
        StockMedicamentoResponseDTO stockDTO = new StockMedicamentoResponseDTO();
        stockDTO.setIdMedicamento(medicamento.getIdMedicamento());
        stockDTO.setNombreMedicamento(medicamento.getNombre());
        stockDTO.setDescripcion(medicamento.getDescripcion());
        stockDTO.setStockTotal(medicamento.getStockTotal());
        
        if (medicamento.getSede() != null) {
            stockDTO.setIdSede(medicamento.getSede().getIdSede());
            stockDTO.setNombreSede(medicamento.getSede().getNombre());
        }
        
        // Contar lotes y lotes próximos a caducar
        int cantidadLotes = lotes.size();
        int lotesProximos = 0;
        
        LocalDate hoy = LocalDate.now();
        for (Lote lote : lotes) {
            long diasRestantes = ChronoUnit.DAYS.between(hoy, lote.getFechaCaducidad());
            if (diasRestantes <= 30 && diasRestantes >= 0) {
                lotesProximos++;
            }
        }
        
        stockDTO.setCantidadLotes(cantidadLotes);
        stockDTO.setLotesProximosCaducar(lotesProximos);
        
        // Determinar estado del stock y clase CSS
        if (medicamento.getStockTotal() < 5) {
            stockDTO.setEstadoStock("CRITICO");
            stockDTO.setClaseCSS("danger"); // Bootstrap: rojo
        } else if (medicamento.getStockTotal() < 10) {
            stockDTO.setEstadoStock("BAJO");
            stockDTO.setClaseCSS("warning"); // Bootstrap: amarillo
        } else {
            stockDTO.setEstadoStock("NORMAL");
            stockDTO.setClaseCSS("success"); // Bootstrap: verde
        }
        
        return stockDTO;
    }
}