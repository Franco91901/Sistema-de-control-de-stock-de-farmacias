package com.proyecto.core.notificacion.application.service;

import com.proyecto.core.lote.domain.model.Lote;
import com.proyecto.core.lote.domain.repository.LoteRepository;
import com.proyecto.core.medicamento.domain.model.MedicamentoSede;
import com.proyecto.core.medicamento.domain.repository.MedicamentoSedeRepository;
import com.proyecto.core.notificacion.application.dto.NotificacionResponseDTO;
import com.proyecto.core.notificacion.application.mapper.NotificacionMapper;
import com.proyecto.core.notificacion.domain.model.EstadoNotificacion;
import com.proyecto.core.notificacion.domain.model.Notificacion;
import com.proyecto.core.notificacion.domain.model.TipoNotificacion;
import com.proyecto.core.notificacion.domain.repository.NotificacionRepository;
import com.proyecto.core.stock.application.service.FarmaceuticoConstants;
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
    private final MedicamentoSedeRepository medicamentoSedeRepository;
    private final LoteRepository loteRepository;
    private final NotificacionMapper notificacionMapper;

    @Override
    public List<NotificacionResponseDTO> listarNotificacionesPorSede(Long idSede) {
        return notificacionRepository.findBySedeIdSedeOrderByFechaDesc(idSede).stream()
                .map(notificacionMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<NotificacionResponseDTO> listarNotificacionesPendientes(Long idSede) {
        return notificacionRepository.findBySedeIdSedeAndEstadoOrderByFechaDesc(idSede, EstadoNotificacion.PENDIENTE).stream()
                .map(notificacionMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<NotificacionResponseDTO> listarNotificacionesPorTipo(Long idSede, TipoNotificacion tipo) {
        return notificacionRepository.findBySedeIdSedeAndTipo(idSede, tipo).stream()
                .map(notificacionMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public NotificacionResponseDTO obtenerNotificacionPorId(Long idNotificacion) {
        Notificacion notificacion = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new com.proyecto.shared.exception.EntityNotFoundException(
                        com.proyecto.shared.exception.ExceptionConstants.NOTIFICACION_NO_ENCONTRADA));
        return notificacionMapper.toResponseDTO(notificacion);
    }

    @Override
    @Transactional
    public void verificarNotificacionBajoStock(MedicamentoSede medSede) {
        if (medSede == null) return;

        boolean bajoStock = medSede.getStockTotal() < FarmaceuticoConstants.UMBRAL_BAJO_STOCK;
        Long idMedicamento = medSede.getMedicamento().getIdMedicamento();

        List<Notificacion> existentes = notificacionRepository.findByMedicamentoIdMedicamento(idMedicamento);
        boolean existePendiente = existentes.stream()
                .anyMatch(n -> n.getTipo() == TipoNotificacion.BAJO_STOCK
                            && n.getEstado() == EstadoNotificacion.PENDIENTE);

        if (bajoStock && !existePendiente) {
            String mensaje = String.format("Medicamento %s tiene stock bajo en sede %s: %d unidades (mínimo: %d)",
                    medSede.getMedicamento().getNombre(), medSede.getSede().getNombre(),
                    medSede.getStockTotal(), FarmaceuticoConstants.UMBRAL_BAJO_STOCK);
            notificacionRepository.save(notificacionMapper.toEntityAuto(mensaje, TipoNotificacion.BAJO_STOCK, medSede));

        } else if (!bajoStock && existePendiente) {
            existentes.stream()
                    .filter(n -> n.getTipo() == TipoNotificacion.BAJO_STOCK
                              && n.getEstado() == EstadoNotificacion.PENDIENTE)
                    .forEach(n -> {
                        n.setEstado(EstadoNotificacion.ATENDIDA);
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
        Long idMedicamento = lote.getMedicamento().getIdMedicamento();

        List<Notificacion> existentes = notificacionRepository.findByMedicamentoIdMedicamento(idMedicamento);
        boolean existePendiente = existentes.stream()
                .anyMatch(n -> n.getTipo() == TipoNotificacion.PROXIMO_CADUCAR
                            && n.getEstado() == EstadoNotificacion.PENDIENTE
                            && n.getMensaje().contains(lote.getCodigoLote()));

        if (proximoCaducar && !existePendiente) {
            String mensaje = String.format("Lote %s del medicamento %s caduca en %d días (Caduca: %s)",
                    lote.getCodigoLote(), lote.getMedicamento().getNombre(),
                    diasParaCaducar, lote.getFechaCaducidad());
            medicamentoSedeRepository
                    .findByMedicamentoIdMedicamentoAndSedeIdSede(idMedicamento, lote.getSede().getIdSede())
                    .ifPresent(ms -> notificacionRepository.save(
                            notificacionMapper.toEntityAuto(mensaje, TipoNotificacion.PROXIMO_CADUCAR, ms)));
        }
    }

    @Override
    @Transactional
    public void generarNotificacionesAutomaticas(Long idSede) {
        medicamentoSedeRepository.findMedicamentosBajoStock(idSede, FarmaceuticoConstants.UMBRAL_BAJO_STOCK)
                .forEach(this::verificarNotificacionBajoStock);

        LocalDate hoy = LocalDate.now();
        loteRepository.findLotesProximosCaducar(idSede, hoy, hoy.plusDays(FarmaceuticoConstants.DIAS_ALERTA_CADUCIDAD))
                .forEach(this::verificarNotificacionCaducidad);
    }

    @Override
    public Long contarNotificacionesPendientes(Long idSede) {
        return notificacionRepository.countBySedeIdSedeAndEstado(idSede, EstadoNotificacion.PENDIENTE);
    }

    @Override
    public Long contarNotificacionesPorTipo(Long idSede, TipoNotificacion tipo) {
        return (long) notificacionRepository.findBySedeIdSedeAndTipo(idSede, tipo).size();
    }
}
