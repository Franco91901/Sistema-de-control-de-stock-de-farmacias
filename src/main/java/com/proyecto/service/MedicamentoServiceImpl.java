package com.proyecto.service;
import com.proyecto.dto.MedicamentoRequestDTO;
import com.proyecto.dto.MedicamentoResponseDTO;
import com.proyecto.dto.StockMedicamentoResponseDTO;
import com.proyecto.mapper.MedicamentoMapper;
import com.proyecto.mapper.StockMapper;
import com.proyecto.model.Medicamento;
import com.proyecto.model.Sede;
import com.proyecto.repository.MedicamentoRepository;
import com.proyecto.repository.LoteRepository;
import com.proyecto.repository.SedeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicamentoServiceImpl implements MedicamentoService {
    
    private final MedicamentoRepository medicamentoRepository;
    private final SedeRepository sedeRepository;
    private final LoteRepository loteRepository;
    private final MedicamentoMapper medicamentoMapper;
    private final StockMapper stockMapper;
    
    @Override
    @Transactional
    public MedicamentoResponseDTO crearMedicamento(MedicamentoRequestDTO requestDTO, Long idSede) {
        // Validar que no exista medicamento con mismo nombre en la sede
        if (existeMedicamentoEnSede(requestDTO.getNombre(), idSede)) {
            throw new IllegalArgumentException("Ya existe un medicamento con ese nombre en esta sede");
        }
        
        // Obtener sede
        Sede sede = sedeRepository.findById(idSede)
                .orElseThrow(() -> new IllegalArgumentException("Sede no encontrada"));
        
        // Crear medicamento
        Medicamento medicamento = medicamentoMapper.toEntity(requestDTO, sede);
        medicamento.setStockTotal(0); // Inicia con stock 0
        
        Medicamento saved = medicamentoRepository.save(medicamento);
        return medicamentoMapper.toResponseDTO(saved);
    }
    
    @Override
    public MedicamentoResponseDTO obtenerMedicamentoPorId(Long idMedicamento) {
        Medicamento medicamento = medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado"));
        return medicamentoMapper.toResponseDTO(medicamento);
    }
    
    @Override
    public List<MedicamentoResponseDTO> listarMedicamentosPorSede(Long idSede) {
        List<Medicamento> medicamentos = medicamentoRepository.findBySedeIdSede(idSede);
        return medicamentos.stream()
                .map(medicamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public MedicamentoResponseDTO actualizarMedicamento(Long idMedicamento, MedicamentoRequestDTO requestDTO) {
        Medicamento medicamento = medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado"));
        
        // Validar que el nuevo nombre no exista en la misma sede (si cambió)
        if (!medicamento.getNombre().equalsIgnoreCase(requestDTO.getNombre())) {
            if (existeMedicamentoEnSede(requestDTO.getNombre(), medicamento.getSede().getIdSede())) {
                throw new IllegalArgumentException("Ya existe un medicamento con ese nombre en esta sede");
            }
        }
        
        medicamentoMapper.updateEntity(medicamento, requestDTO);
        Medicamento updated = medicamentoRepository.save(medicamento);
        return medicamentoMapper.toResponseDTO(updated);
    }
    
    @Override
    @Transactional
    public void eliminarMedicamento(Long idMedicamento) {
        // Verificar que no tenga lotes asociados
        List<com.proyecto.model.Lote> lotes = loteRepository.findByMedicamentoIdMedicamento(idMedicamento);
        if (!lotes.isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el medicamento porque tiene lotes asociados");
        }
        
        medicamentoRepository.deleteById(idMedicamento);
    }
    
    @Override
    public boolean existeMedicamentoEnSede(String nombre, Long idSede) {
        return medicamentoRepository.findByNombreIgnoreCaseAndSede(nombre, idSede).isPresent();
    }
    
    @Override
    public boolean validarStockSuficiente(Long idMedicamento, Integer cantidad) {
        Medicamento medicamento = medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado"));
        return medicamento.getStockTotal() >= cantidad;
    }
    
    @Override
    public StockMedicamentoResponseDTO obtenerStockMedicamento(Long idMedicamento) {
        Medicamento medicamento = medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado"));
        
        List<com.proyecto.model.Lote> lotes = loteRepository.findByMedicamentoIdMedicamento(idMedicamento);
        return stockMapper.toStockResponseDTO(medicamento, lotes);
    }
    
    @Override
    public List<StockMedicamentoResponseDTO> listarStockPorSede(Long idSede) {
        List<Medicamento> medicamentos = medicamentoRepository.findBySedeIdSede(idSede);
        
        return medicamentos.stream()
                .map(med -> {
                    List<com.proyecto.model.Lote> lotes = loteRepository.findByMedicamentoIdMedicamento(med.getIdMedicamento());
                    return stockMapper.toStockResponseDTO(med, lotes);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MedicamentoResponseDTO> buscarMedicamentosPorNombre(String nombre, Long idSede) {
        List<Medicamento> medicamentos = medicamentoRepository.findBySedeIdSede(idSede);
        
        return medicamentos.stream()
                .filter(m -> m.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .map(medicamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MedicamentoResponseDTO> buscarMedicamentosBajoStock(Long idSede) {
        List<Medicamento> medicamentosBajoStock = medicamentoRepository
                .findMedicamentosBajoStock(idSede, FarmaceuticoConstants.UMBRAL_BAJO_STOCK);
        
        return medicamentosBajoStock.stream()
                .map(medicamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
