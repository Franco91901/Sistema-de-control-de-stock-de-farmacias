package com.proyecto.core.lote.application.service;

import com.proyecto.core.lote.application.dto.LoteRequestDTO;
import com.proyecto.core.lote.application.dto.LoteResponseDTO;
import com.proyecto.core.lote.application.dto.LoteStockResponseDTO;

import java.util.List;

public interface LoteService {

    LoteResponseDTO crearLote(LoteRequestDTO requestDTO);
    LoteResponseDTO obtenerLotePorId(Long idLote);
    List<LoteResponseDTO> listarLotesPorMedicamento(Long idMedicamento);
    List<LoteResponseDTO> listarLotesPorSede(Long idSede);
    LoteResponseDTO actualizarLote(Long idLote, LoteRequestDTO requestDTO);
    void eliminarLote(Long idLote);

    LoteResponseDTO retirarStock(Long idLote, Integer cantidad);
    List<LoteResponseDTO> retirarLotesVencidos(Long idSede);
    void actualizarStockTotalMedicamentoEnSede(Long idMedicamento, Long idSede);

    List<LoteStockResponseDTO> listarLotesParaStock(Long idSede);
    List<LoteResponseDTO> listarLotesProximosCaducar(Long idSede);
    List<LoteResponseDTO> listarLotesCaducados(Long idSede);

    boolean existeCodigoLote(String codigoLote);
}
