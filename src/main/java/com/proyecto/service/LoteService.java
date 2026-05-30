package com.proyecto.service;


import com.proyecto.dto.LoteRequestDTO;
import com.proyecto.dto.LoteResponseDTO;
import com.proyecto.dto.LoteStockResponseDTO;
import java.util.List;

public interface LoteService {
    
    // CRUD básico
    LoteResponseDTO crearLote(LoteRequestDTO requestDTO);
    LoteResponseDTO obtenerLotePorId(Long idLote);
    List<LoteResponseDTO> listarLotesPorMedicamento(Long idMedicamento);
    List<LoteResponseDTO> listarLotesPorSede(Long idSede);
    LoteResponseDTO actualizarLote(Long idLote, LoteRequestDTO requestDTO);
    void eliminarLote(Long idLote);
    
    // Operaciones de stock
    LoteResponseDTO retirarStock(Long idLote, Integer cantidad);
    List<LoteResponseDTO> retirarLotesVencidos(Long idSede);
    void actualizarStockTotalMedicamento(Long idMedicamento);
    
    // Consultas especiales
    List<LoteStockResponseDTO> listarLotesParaStock(Long idSede);
    List<LoteResponseDTO> listarLotesProximosCaducar(Long idSede);
    List<LoteResponseDTO> listarLotesCaducados(Long idSede);
    
    // Validaciones
    boolean existeCodigoLote(String codigoLote);
}
