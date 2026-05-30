package com.proyecto.core.medicamento.application.service;

import com.proyecto.core.medicamento.application.dto.MedicamentoRequestDTO;
import com.proyecto.core.medicamento.application.dto.MedicamentoResponseDTO;
import com.proyecto.core.stock.application.dto.StockMedicamentoResponseDTO;
import java.util.List;

public interface MedicamentoService {
    
    // CRUD básico
    MedicamentoResponseDTO crearMedicamento(MedicamentoRequestDTO requestDTO, Long idSede);
    MedicamentoResponseDTO obtenerMedicamentoPorId(Long idMedicamento);
    List<MedicamentoResponseDTO> listarMedicamentosPorSede(Long idSede);
    MedicamentoResponseDTO actualizarMedicamento(Long idMedicamento, MedicamentoRequestDTO requestDTO);
    void eliminarMedicamento(Long idMedicamento);
    
    // Validaciones
    boolean existeMedicamentoEnSede(String nombre, Long idSede);
    boolean validarStockSuficiente(Long idMedicamento, Integer cantidad);
    
    // Stock
    StockMedicamentoResponseDTO obtenerStockMedicamento(Long idMedicamento);
    List<StockMedicamentoResponseDTO> listarStockPorSede(Long idSede);
    
    // Búsquedas
    List<MedicamentoResponseDTO> buscarMedicamentosPorNombre(String nombre, Long idSede);
    List<MedicamentoResponseDTO> buscarMedicamentosBajoStock(Long idSede);
}