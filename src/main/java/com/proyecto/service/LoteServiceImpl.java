package com.proyecto.service;

import com.proyecto.dto.LoteRequestDTO;
import com.proyecto.dto.LoteResponseDTO;
import com.proyecto.dto.LoteStockResponseDTO;
import com.proyecto.mapper.LoteMapper;
import com.proyecto.model.Lote;
import com.proyecto.model.Medicamento;
import com.proyecto.repository.LoteRepository;
import com.proyecto.repository.MedicamentoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoteServiceImpl implements LoteService {
    
    private final LoteRepository loteRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final LoteMapper loteMapper;
    private final NotificacionService notificacionService;
    
    @Override
    @Transactional
    public LoteResponseDTO crearLote(LoteRequestDTO requestDTO) {
        // Validar código único
        if (existeCodigoLote(requestDTO.getCodigoLote())) {
            throw new IllegalArgumentException("Ya existe un lote con ese código");
        }
        
        // Obtener medicamento
        Medicamento medicamento = medicamentoRepository.findById(requestDTO.getIdMedicamento())
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado"));
        
        // Crear lote
        Lote lote = loteMapper.toEntity(requestDTO, medicamento);
        Lote saved = loteRepository.save(lote);
        
        // Actualizar stock total del medicamento
        actualizarStockTotalMedicamento(medicamento.getIdMedicamento());
        
        // Verificar notificaciones de bajo stock (si pasó de bajo a normal)
        notificacionService.verificarNotificacionBajoStock(medicamento);
        
        // Verificar notificación de caducidad
        notificacionService.verificarNotificacionCaducidad(saved);
        
        return loteMapper.toResponseDTO(saved);
    }
    
    @Override
    public LoteResponseDTO obtenerLotePorId(Long idLote) {
        Lote lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
        return loteMapper.toResponseDTO(lote);
    }
    
    @Override
    public List<LoteResponseDTO> listarLotesPorMedicamento(Long idMedicamento) {
        List<Lote> lotes = loteRepository.findByMedicamentoIdMedicamento(idMedicamento);
        return lotes.stream()
                .map(loteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<LoteResponseDTO> listarLotesPorSede(Long idSede) {
        List<Lote> lotes = loteRepository.findBySedeId(idSede);
        return lotes.stream()
                .map(loteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public LoteResponseDTO actualizarLote(Long idLote, LoteRequestDTO requestDTO) {
        Lote lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
        
        // Validar código único (si cambió)
        if (!lote.getCodigoLote().equals(requestDTO.getCodigoLote())) {
            if (existeCodigoLote(requestDTO.getCodigoLote())) {
                throw new IllegalArgumentException("Ya existe un lote con ese código");
            }
        }
        
        // Guardar stock anterior para comparar
        Integer stockAnterior = lote.getStockLote();
        
        // Actualizar lote
        loteMapper.updateEntity(lote, requestDTO);
        Lote updated = loteRepository.save(lote);
        
        // Si cambió el stock, actualizar medicamento
        if (!stockAnterior.equals(requestDTO.getStockLote())) {
            actualizarStockTotalMedicamento(lote.getMedicamento().getIdMedicamento());
            
            // Verificar notificaciones de bajo stock
            notificacionService.verificarNotificacionBajoStock(lote.getMedicamento());
        }
        
        // Verificar notificación de caducidad
        notificacionService.verificarNotificacionCaducidad(updated);
        
        return loteMapper.toResponseDTO(updated);
    }
    
    @Override
    @Transactional
    public void eliminarLote(Long idLote) {
        Lote lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
        
        Long idMedicamento = lote.getMedicamento().getIdMedicamento();
        loteRepository.delete(lote);
        
        // Actualizar stock total del medicamento
        actualizarStockTotalMedicamento(idMedicamento);
        
        // Verificar notificaciones de bajo stock
        notificacionService.verificarNotificacionBajoStock(lote.getMedicamento());
    }
    
    @Override
    @Transactional
    public LoteResponseDTO retirarStock(Long idLote, Integer cantidad) {
        Lote lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
        
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a retirar debe ser mayor a cero");
        }
        
        if (lote.getStockLote() < cantidad) {
            throw new IllegalStateException("Stock insuficiente en el lote. Stock disponible: " + lote.getStockLote());
        }
        
        // Retirar stock
        lote.setStockLote(lote.getStockLote() - cantidad);
        Lote updated = loteRepository.save(lote);
        
        // Actualizar stock total del medicamento
        actualizarStockTotalMedicamento(lote.getMedicamento().getIdMedicamento());
        
        // Verificar notificaciones de bajo stock
        notificacionService.verificarNotificacionBajoStock(lote.getMedicamento());
        
        return loteMapper.toResponseDTO(updated);
    }
    
    @Override
    @Transactional
    public List<LoteResponseDTO> retirarLotesVencidos(Long idSede) {
        LocalDate hoy = LocalDate.now();
        List<Lote> lotes = loteRepository.findBySedeId(idSede);
        
        List<Lote> lotesVencidos = lotes.stream()
                .filter(l -> l.getFechaCaducidad().isBefore(hoy))
                .collect(Collectors.toList());
        
        // Eliminar lotes vencidos
        for (Lote lote : lotesVencidos) {
            Long idMedicamento = lote.getMedicamento().getIdMedicamento();
            loteRepository.delete(lote);
            actualizarStockTotalMedicamento(idMedicamento);
        }
        
        return lotesVencidos.stream()
                .map(loteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void actualizarStockTotalMedicamento(Long idMedicamento) {
        Integer stockTotal = loteRepository.sumStockByMedicamento(idMedicamento);
        
        Medicamento medicamento = medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado"));
        
        medicamento.setStockTotal(stockTotal != null ? stockTotal : 0);
        medicamentoRepository.save(medicamento);
    }
    
    @Override
    public List<LoteStockResponseDTO> listarLotesParaStock(Long idSede) {
        List<Lote> lotes = loteRepository.findBySedeId(idSede);
        return lotes.stream()
                .map(loteMapper::toStockResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<LoteResponseDTO> listarLotesProximosCaducar(Long idSede) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(FarmaceuticoConstants.DIAS_ALERTA_CADUCIDAD);
        
        List<Lote> lotes = loteRepository.findLotesProximosCaducar(idSede, hoy, fechaLimite);
        return lotes.stream()
                .map(loteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<LoteResponseDTO> listarLotesCaducados(Long idSede) {
        LocalDate hoy = LocalDate.now();
        List<Lote> lotes = loteRepository.findBySedeId(idSede);
        
        return lotes.stream()
                .filter(l -> l.getFechaCaducidad().isBefore(hoy))
                .map(loteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existeCodigoLote(String codigoLote) {
        return loteRepository.findByCodigoLote(codigoLote).isPresent();
    }
}