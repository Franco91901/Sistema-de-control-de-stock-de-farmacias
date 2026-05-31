package com.proyecto.core.stock.application.mapper;

import com.proyecto.core.medicamento.domain.model.MedicamentoSede;
import com.proyecto.core.stock.application.dto.StockMedicamentoResponseDTO;
import com.proyecto.core.lote.domain.model.Lote;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class StockMapper {

    public StockMedicamentoResponseDTO toStockResponseDTO(MedicamentoSede medSede, List<Lote> lotes) {
        if (medSede == null) return null;
        Long idSede = null;
        String nombreSede = null;
        if (medSede.getSede() != null) {
            idSede = medSede.getSede().getIdSede();
            nombreSede = medSede.getSede().getNombre();
        }

        Integer stockTotal = medSede.getStockTotal();

        LocalDate hoy = LocalDate.now();
        int lotesProximos = (int) lotes.stream()
            .filter(l -> {
                long dias = ChronoUnit.DAYS.between(hoy, l.getFechaCaducidad());
                return dias >= 0 && dias <= 30;
            })
            .count();

        String estadoStock;
        String claseCSS;

        if (stockTotal < 5) {
            estadoStock = "CRITICO";
            claseCSS = "danger";
        } else if (stockTotal < 10) {
            estadoStock = "BAJO";
            claseCSS = "warning";
        } else {
            estadoStock = "NORMAL";
            claseCSS = "success";
        }

        return new StockMedicamentoResponseDTO(
            medSede.getMedicamento().getIdMedicamento(),
            medSede.getMedicamento().getNombre(),
            medSede.getMedicamento().getDescripcion(),
            stockTotal,
            idSede,
            nombreSede,
            lotes.size(),
            lotesProximos,
            estadoStock,
            claseCSS
        );
    }
}
