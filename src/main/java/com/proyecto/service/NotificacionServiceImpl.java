package com.proyecto.service;

import com.proyecto.dto.NotificacionResponseDTO;
import com.proyecto.mapper.NotificacionMapper;
import com.proyecto.model.Lote;
import com.proyecto.model.Medicamento;
import com.proyecto.model.Notificacion;
import com.proyecto.repository.NotificacionRepository;
import com.proyecto.repository.MedicamentoRepository;
import com.proyecto.repository.LoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {
    
    private final NotificacionRepository notificacionRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final LoteRepository loteRepository;
    private final NotificacionMapper notificacionMapper;
    
    @Override
    public List<NotificacionResponseDTO> listarNotificacionesPorSede(Long idSede) {
        // Usa el método que ya ordena por fecha descendente
        List<Notificacion> notificaciones = notificacionRepository.findBySedeIdSedeOrderByFechaDesc(idSede);
        return notificaciones.stream()
                .map(notificacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<NotificacionResponseDTO> listarNotificacionesPendientes(Long idSede) {
        // Usa el método que ya ordena por fecha descendente
        List<Notificacion> notificaciones = notificacionRepository.findBySedeIdSedeAndEstadoOrderByFechaDesc(idSede, FarmaceuticoConstants.ESTADO_NOTIF_PENDIENTE);
        return notificaciones.stream()
                .map(notificacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<NotificacionResponseDTO> listarNotificacionesPorTipo(Long idSede, String tipo) {
        List<Notificacion> notificaciones = notificacionRepository.findBySedeIdSedeAndTipo(idSede, tipo);
        return notificaciones.stream()
                .map(notificacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public NotificacionResponseDTO obtenerNotificacionPorId(Long idNotificacion) {
        Notificacion notificacion = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));
        return notificacionMapper.toResponseDTO(notificacion);
    }
    
    @Override
    @Transactional
    public void verificarNotificacionBajoStock(Medicamento medicamento) {
        if (medicamento == null) return;
        
        boolean bajoStock = medicamento.getStockTotal() < FarmaceuticoConstants.UMBRAL_BAJO_STOCK;
        String tipo = FarmaceuticoConstants.TIPO_NOTIF_BAJO_STOCK;
        
        // Buscar si ya existe una notificación pendiente de bajo stock para este medicamento
        List<Notificacion> notificacionesExistentes = notificacionRepository
                .findByMedicamentoIdMedicamento(medicamento.getIdMedicamento());
        
        boolean existeNotifPendiente = notificacionesExistentes.stream()
                .anyMatch(n -> n.getTipo().equals(tipo) && 
                              n.getEstado().equals(FarmaceuticoConstants.ESTADO_NOTIF_PENDIENTE));
        
        if (bajoStock && !existeNotifPendiente) {
            // Crear nueva notificación de bajo stock
            String mensaje = String.format("Medicamento %s tiene stock bajo: %d unidades (mínimo: %d)",
                    medicamento.getNombre(), medicamento.getStockTotal(), FarmaceuticoConstants.UMBRAL_BAJO_STOCK);
            
            Notificacion notificacion = notificacionMapper.toEntityAuto(mensaje, tipo, medicamento);
            notificacionRepository.save(notificacion);
            
        } else if (!bajoStock && existeNotifPendiente) {
            // Marcar como ATENDIDA si ya no está bajo stock
            notificacionesExistentes.stream()
                    .filter(n -> n.getTipo().equals(tipo) && 
                               n.getEstado().equals(FarmaceuticoConstants.ESTADO_NOTIF_PENDIENTE))
                    .forEach(n -> {
                        n.setEstado(FarmaceuticoConstants.ESTADO_NOTIF_ATENDIDA);
                        notificacionRepository.save(n);
                    });
        }
    }
    
    @Override
    @Transactional
    public void verificarNotificacionCaducidad(Lote lote) {
        if (lote == null) return;
        
        LocalDate hoy = LocalDate.now();
        long diasParaCaducar = ChronoUnit.DAYS.between(hoy, lote.getFechaCaducidad());
        
        boolean proximoCaducar = diasParaCaducar <= FarmaceuticoConstants.DIAS_ALERTA_CADUCIDAD && diasParaCaducar >= 0;
        String tipo = FarmaceuticoConstants.TIPO_NOTIF_PROXIMO_CADUCAR;
        
        // Buscar notificaciones existentes para este lote
        List<Notificacion> notificacionesExistentes = notificacionRepository
                .findByMedicamentoIdMedicamento(lote.getMedicamento().getIdMedicamento());
        
        boolean existeNotifPendiente = notificacionesExistentes.stream()
                .anyMatch(n -> n.getTipo().equals(tipo) && 
                              n.getEstado().equals(FarmaceuticoConstants.ESTADO_NOTIF_PENDIENTE) &&
                              n.getMensaje().contains(lote.getCodigoLote()));
        
        if (proximoCaducar && !existeNotifPendiente) {
            // Crear nueva notificación de próxima caducidad
            String mensaje = String.format("Lote %s del medicamento %s caduca en %d días (Caduca: %s)",
                    lote.getCodigoLote(), lote.getMedicamento().getNombre(), 
                    diasParaCaducar, lote.getFechaCaducidad());
            
            Notificacion notificacion = notificacionMapper.toEntityAuto(mensaje, tipo, lote.getMedicamento());
            notificacionRepository.save(notificacion);
        }
    }
    
    @Override
    @Transactional
    public void generarNotificacionesAutomaticas(Long idSede) {
        // Verificar medicamentos con bajo stock
        List<Medicamento> medicamentosBajoStock = medicamentoRepository
                .findMedicamentosBajoStock(idSede, FarmaceuticoConstants.UMBRAL_BAJO_STOCK);
        
        for (Medicamento medicamento : medicamentosBajoStock) {
            verificarNotificacionBajoStock(medicamento);
        }
        
        // Verificar lotes próximos a caducar
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(FarmaceuticoConstants.DIAS_ALERTA_CADUCIDAD);
        
        List<Lote> lotesProximos = loteRepository.findLotesProximosCaducar(idSede, hoy, fechaLimite);
        for (Lote lote : lotesProximos) {
            verificarNotificacionCaducidad(lote);
        }
    }
    
    @Override
    public Long contarNotificacionesPendientes(Long idSede) {
        return notificacionRepository.countBySedeIdSedeAndEstado(idSede, FarmaceuticoConstants.ESTADO_NOTIF_PENDIENTE);
    }
    
    @Override
    public Long contarNotificacionesPorTipo(Long idSede, String tipo) {
        return notificacionRepository.findBySedeIdSedeAndTipo(idSede, tipo).stream().count();
    }
}