package com.proyecto.core.lote.application.service;

import com.proyecto.auth.domain.model.Usuario;
import com.proyecto.core.lote.application.dto.LoteRequestDTO;
import com.proyecto.core.lote.application.dto.LoteResponseDTO;
import com.proyecto.core.lote.application.dto.LoteStockResponseDTO;
import com.proyecto.core.lote.application.mapper.LoteMapper;
import com.proyecto.core.lote.domain.model.Lote;
import com.proyecto.core.medicamento.domain.model.Medicamento;
import com.proyecto.core.medicamento.domain.model.MedicamentoSede;
import com.proyecto.core.medicamento.domain.repository.MedicamentoRepository;
import com.proyecto.core.medicamento.domain.repository.MedicamentoSedeRepository;
import com.proyecto.core.lote.domain.repository.LoteRepository;
import com.proyecto.core.sede.domain.model.Sede;
import com.proyecto.core.sede.domain.repository.SedeRepository;
import com.proyecto.core.stock.application.service.FarmaceuticoConstants;
import com.proyecto.core.stock.domain.model.MovimientoStock;
import com.proyecto.core.stock.domain.model.TipoMovimiento;
import com.proyecto.core.stock.domain.repository.MovimientoStockRepository;
import com.proyecto.core.notificacion.application.service.NotificacionService;
import com.proyecto.shared.exception.EntityNotFoundException;
import com.proyecto.shared.exception.ExceptionConstants;
import com.proyecto.shared.security.AuthContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoteServiceImpl implements LoteService {

    private final LoteRepository loteRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final MedicamentoSedeRepository medicamentoSedeRepository;
    private final SedeRepository sedeRepository;
    private final MovimientoStockRepository movimientoStockRepository;
    private final AuthContext authContext;
    private final LoteMapper loteMapper;
    private final NotificacionService notificacionService;

    @Override
    @Transactional
    public LoteResponseDTO crearLote(LoteRequestDTO requestDTO) {
        if (existeCodigoLote(requestDTO.codigoLote())) {
            throw new IllegalArgumentException("Ya existe un lote con ese código");
        }

        Medicamento medicamento = medicamentoRepository.findById(requestDTO.idMedicamento())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.MEDICAMENTO_NO_ENCONTRADO));
        Sede sede = sedeRepository.findById(requestDTO.idSede())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.SEDE_NO_ENCONTRADA));

        Lote lote = loteMapper.toEntity(requestDTO, medicamento, sede);
        Lote saved = loteRepository.save(lote);

        actualizarStockTotalMedicamentoEnSede(medicamento.getIdMedicamento(), sede.getIdSede());
        registrarMovimiento(medicamento, sede, saved, TipoMovimiento.ENTRADA, saved.getStockLote(),
                "Nuevo lote: " + saved.getCodigoLote());

        medicamentoSedeRepository
                .findByMedicamentoIdMedicamentoAndSedeIdSede(medicamento.getIdMedicamento(), sede.getIdSede())
                .ifPresent(notificacionService::verificarNotificacionBajoStock);
        notificacionService.verificarNotificacionCaducidad(saved);

        return loteMapper.toResponseDTO(saved);
    }

    @Override
    public LoteResponseDTO obtenerLotePorId(Long idLote) {
        return loteMapper.toResponseDTO(findLoteOrThrow(idLote));
    }

    @Override
    public List<LoteResponseDTO> listarLotesPorMedicamento(Long idMedicamento) {
        return loteRepository.findByMedicamentoIdMedicamento(idMedicamento).stream()
                .map(loteMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<LoteResponseDTO> listarLotesPorSede(Long idSede) {
        return loteRepository.findBySedeId(idSede).stream()
                .map(loteMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LoteResponseDTO actualizarLote(Long idLote, LoteRequestDTO requestDTO) {
        Lote lote = findLoteOrThrow(idLote);

        if (!lote.getCodigoLote().equals(requestDTO.codigoLote()) && existeCodigoLote(requestDTO.codigoLote())) {
            throw new IllegalArgumentException("Ya existe un lote con ese código");
        }

        Integer stockAnterior = lote.getStockLote();
        loteMapper.updateEntity(lote, requestDTO);
        Lote updated = loteRepository.save(lote);

        if (!stockAnterior.equals(requestDTO.stockLote())) {
            Long idMedicamento = lote.getMedicamento().getIdMedicamento();
            Long idSede = lote.getSede().getIdSede();
            actualizarStockTotalMedicamentoEnSede(idMedicamento, idSede);

            int diferencia = requestDTO.stockLote() - stockAnterior;
            TipoMovimiento tipo = diferencia > 0 ? TipoMovimiento.ENTRADA : TipoMovimiento.AJUSTE;
            registrarMovimiento(lote.getMedicamento(), lote.getSede(), lote, tipo,
                    Math.abs(diferencia), "Ajuste en lote: " + lote.getCodigoLote());

            medicamentoSedeRepository
                    .findByMedicamentoIdMedicamentoAndSedeIdSede(idMedicamento, idSede)
                    .ifPresent(notificacionService::verificarNotificacionBajoStock);
        }
        notificacionService.verificarNotificacionCaducidad(updated);
        return loteMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void eliminarLote(Long idLote) {
        Lote lote = findLoteOrThrow(idLote);
        Long idMedicamento = lote.getMedicamento().getIdMedicamento();
        Long idSede = lote.getSede().getIdSede();

        registrarMovimiento(lote.getMedicamento(), lote.getSede(), lote, TipoMovimiento.AJUSTE,
                lote.getStockLote(), "Eliminación de lote: " + lote.getCodigoLote());

        loteRepository.delete(lote);
        actualizarStockTotalMedicamentoEnSede(idMedicamento, idSede);

        medicamentoSedeRepository
                .findByMedicamentoIdMedicamentoAndSedeIdSede(idMedicamento, idSede)
                .ifPresent(notificacionService::verificarNotificacionBajoStock);
    }

    @Override
    @Transactional
    public LoteResponseDTO retirarStock(Long idLote, Integer cantidad) {
        Lote lote = findLoteOrThrow(idLote);
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad a retirar debe ser mayor a cero");
        if (lote.getStockLote() < cantidad) {
            throw new IllegalStateException("Stock insuficiente en el lote. Disponible: " + lote.getStockLote());
        }

        lote.setStockLote(lote.getStockLote() - cantidad);
        Lote updated = loteRepository.save(lote);

        Long idMedicamento = lote.getMedicamento().getIdMedicamento();
        Long idSede = lote.getSede().getIdSede();
        actualizarStockTotalMedicamentoEnSede(idMedicamento, idSede);
        registrarMovimiento(lote.getMedicamento(), lote.getSede(), lote, TipoMovimiento.SALIDA, cantidad,
                "Retiro de stock — lote: " + lote.getCodigoLote());

        medicamentoSedeRepository
                .findByMedicamentoIdMedicamentoAndSedeIdSede(idMedicamento, idSede)
                .ifPresent(notificacionService::verificarNotificacionBajoStock);

        return loteMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public List<LoteResponseDTO> retirarLotesVencidos(Long idSede) {
        LocalDate hoy = LocalDate.now();
        List<Lote> vencidos = loteRepository.findBySedeId(idSede).stream()
                .filter(l -> l.getFechaCaducidad().isBefore(hoy))
                .collect(Collectors.toList());

        for (Lote lote : vencidos) {
            registrarMovimiento(lote.getMedicamento(), lote.getSede(), lote, TipoMovimiento.VENCIMIENTO,
                    lote.getStockLote(), "Lote vencido retirado: " + lote.getCodigoLote());
            loteRepository.delete(lote);
            actualizarStockTotalMedicamentoEnSede(lote.getMedicamento().getIdMedicamento(), idSede);
        }
        return vencidos.stream().map(loteMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void actualizarStockTotalMedicamentoEnSede(Long idMedicamento, Long idSede) {
        Integer stockTotal = loteRepository.sumStockByMedicamentoAndSede(idMedicamento, idSede);
        medicamentoSedeRepository.findByMedicamentoIdMedicamentoAndSedeIdSede(idMedicamento, idSede)
                .ifPresent(ms -> {
                    ms.setStockTotal(stockTotal != null ? stockTotal : 0);
                    medicamentoSedeRepository.save(ms);
                });
    }

    @Override
    public List<LoteStockResponseDTO> listarLotesParaStock(Long idSede) {
        return loteRepository.findBySedeId(idSede).stream()
                .map(loteMapper::toStockResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<LoteResponseDTO> listarLotesProximosCaducar(Long idSede) {
        LocalDate hoy = LocalDate.now();
        return loteRepository.findLotesProximosCaducar(idSede, hoy, hoy.plusDays(FarmaceuticoConstants.DIAS_ALERTA_CADUCIDAD))
                .stream().map(loteMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<LoteResponseDTO> listarLotesCaducados(Long idSede) {
        LocalDate hoy = LocalDate.now();
        return loteRepository.findBySedeId(idSede).stream()
                .filter(l -> l.getFechaCaducidad().isBefore(hoy))
                .map(loteMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public boolean existeCodigoLote(String codigoLote) {
        return loteRepository.findByCodigoLote(codigoLote).isPresent();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Lote findLoteOrThrow(Long idLote) {
        return loteRepository.findById(idLote)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.LOTE_NO_ENCONTRADO));
    }

    private void registrarMovimiento(Medicamento medicamento, Sede sede, Lote lote,
                                     TipoMovimiento tipo, Integer cantidad, String observacion) {
        Usuario usuario = null;
        try { usuario = authContext.getUsuario(); } catch (Exception ignored) {}

        MovimientoStock mov = new MovimientoStock();
        mov.setMedicamento(medicamento);
        mov.setSede(sede);
        mov.setLote(lote);
        mov.setTipo(tipo);
        mov.setCantidad(cantidad);
        mov.setFecha(LocalDateTime.now());
        mov.setUsuario(usuario);
        mov.setObservacion(observacion);
        movimientoStockRepository.save(mov);
    }
}
