package com.proyecto.core.lote.application.mapper;

import com.proyecto.core.lote.application.dto.LoteRequestDTO;
import com.proyecto.core.lote.application.dto.LoteResponseDTO;
import com.proyecto.core.lote.application.dto.LoteStockResponseDTO;
import com.proyecto.core.lote.domain.model.Lote;
import com.proyecto.core.medicamento.domain.model.Medicamento;
import com.proyecto.core.sede.domain.model.Sede;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class LoteMapper {

    public Lote toEntity(LoteRequestDTO dto, Medicamento medicamento, Sede sede) {
        if (dto == null) return null;
        Lote lote = new Lote();
        lote.setMedicamento(medicamento);
        lote.setSede(sede);
        lote.setCodigoLote(dto.codigoLote());
        lote.setFechaCaducidad(dto.fechaCaducidad());
        lote.setStockLote(dto.stockLote());
        return lote;
    }

    public LoteResponseDTO toResponseDTO(Lote lote) {
        if (lote == null) return null;
        LocalDate hoy = LocalDate.now();
        long dias = ChronoUnit.DAYS.between(hoy, lote.getFechaCaducidad());
        Long idMedicamento = null;
        String nombreMedicamento = null;
        String descripcionMedicamento = null;
        if (lote.getMedicamento() != null) {
            idMedicamento = lote.getMedicamento().getIdMedicamento();
            nombreMedicamento = lote.getMedicamento().getNombre();
            descripcionMedicamento = lote.getMedicamento().getDescripcion();
        }
        Long idSede = null;
        String nombreSede = null;
        if (lote.getSede() != null) {
            idSede = lote.getSede().getIdSede();
            nombreSede = lote.getSede().getNombre();
        }
        return new LoteResponseDTO(
            lote.getIdLote(),
            lote.getCodigoLote(),
            lote.getFechaCaducidad(),
            lote.getStockLote(),
            idMedicamento,
            nombreMedicamento,
            descripcionMedicamento,
            idSede,
            nombreSede,
            dias <= 30,
            (int) dias
        );
    }

    public LoteStockResponseDTO toStockResponseDTO(Lote lote) {
        if (lote == null) return null;
        LocalDate hoy = LocalDate.now();
        long diasRestantes = ChronoUnit.DAYS.between(hoy, lote.getFechaCaducidad());
        String estado;
        if (diasRestantes < 0) estado = "CADUCADO";
        else if (diasRestantes <= 30) estado = "PROXIMO";
        else estado = "VIGENTE";
        Long idMedicamento = null;
        String nombreMedicamento = null;
        if (lote.getMedicamento() != null) {
            idMedicamento = lote.getMedicamento().getIdMedicamento();
            nombreMedicamento = lote.getMedicamento().getNombre();
        }
        return new LoteStockResponseDTO(
            lote.getIdLote(),
            lote.getCodigoLote(),
            lote.getFechaCaducidad(),
            lote.getStockLote(),
            estado,
            (int) diasRestantes,
            nombreMedicamento,
            idMedicamento
        );
    }

    public void updateEntity(Lote lote, LoteRequestDTO dto) {
        if (lote == null || dto == null) return;
        lote.setCodigoLote(dto.codigoLote());
        lote.setFechaCaducidad(dto.fechaCaducidad());
        lote.setStockLote(dto.stockLote());
    }
}
