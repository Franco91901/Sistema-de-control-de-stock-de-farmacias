package com.proyecto.core.stock.application.mapper;

import com.proyecto.core.stock.application.dto.StockMedicamentoResponseDTO;
import com.proyecto.core.medicamento.domain.model.Medicamento;
import com.proyecto.core.lote.domain.model.Lote;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class StockMapper {

    public StockMedicamentoResponseDTO toStockResponseDTO(Medicamento medicamento, List<Lote> lotes) {
        if (medicamento == null) return null;
        Long idSede = null;
        String nombreSede = null;
        if (medicamento.getSede() != null) {
            idSede = medicamento.getSede().getIdSede();
            nombreSede = medicamento.getSede().getNombre();
        }
        LocalDate hoy = LocalDate.now();
        int lotesProximos = (int) lotes.stream()
            .filter(l -> {
                long dias = ChronoUnit.DAYS.between(hoy, l.getFechaCaducidad());
                return dias >= 0 && dias <= 30;
            })
            .count();
        String estadoStock;
        String claseCSS;
        if (medicamento.getStockTotal() < 5) {
            estadoStock = "CRITICO";
            claseCSS = "danger";
        } else if (medicamento.getStockTotal() < 10) {
            estadoStock = "BAJO";
            claseCSS = "warning";
        } else {
            estadoStock = "NORMAL";
            claseCSS = "success";
        }
        return new StockMedicamentoResponseDTO(
            medicamento.getIdMedicamento(),
            medicamento.getNombre(),
            medicamento.getDescripcion(),
            medicamento.getStockTotal(),
            idSede,
            nombreSede,
            lotes.size(),
            lotesProximos,
            estadoStock,
            claseCSS
        );
    }
}
