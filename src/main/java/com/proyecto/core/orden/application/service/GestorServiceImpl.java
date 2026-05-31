package com.proyecto.core.orden.application.service;

import com.proyecto.core.notificacion.application.dto.NotificacionResponseDTO;
import com.proyecto.core.stock.application.dto.StockPorMedicamentoDto;
import com.proyecto.core.stock.application.dto.StockPorSedeDto;
import com.proyecto.core.medicamento.domain.model.Medicamento;
import com.proyecto.core.medicamento.domain.model.MedicamentoSede;
import com.proyecto.core.lote.domain.model.Lote;
import com.proyecto.core.sede.domain.model.Sede;
import com.proyecto.core.notificacion.domain.model.Notificacion;
import com.proyecto.core.orden.domain.model.Orden;
import com.proyecto.core.orden.domain.model.DetalleOrden;
import com.proyecto.auth.domain.model.Usuario;
import com.proyecto.core.medicamento.domain.repository.MedicamentoRepository;
import com.proyecto.core.medicamento.domain.repository.MedicamentoSedeRepository;
import com.proyecto.core.notificacion.application.mapper.NotificacionMapper;
import com.proyecto.core.notificacion.domain.repository.NotificacionRepository;
import com.proyecto.core.orden.domain.repository.OrdenRepository;
import com.proyecto.core.orden.domain.repository.DetalleOrdenRepository;
import com.proyecto.core.sede.domain.repository.SedeRepository;
import com.proyecto.core.lote.domain.repository.LoteRepository;
import com.proyecto.auth.domain.repository.UsuarioRepository;
import com.proyecto.core.stock.application.service.FarmaceuticoConstants;
import com.proyecto.shared.exception.EntityNotFoundException;
import com.proyecto.shared.exception.ExceptionConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GestorServiceImpl implements GestorService {

    private final NotificacionRepository notificacionRepository;
    private final OrdenRepository ordenRepository;
    private final DetalleOrdenRepository detalleOrdenRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final MedicamentoSedeRepository medicamentoSedeRepository;
    private final SedeRepository sedeRepository;
    private final LoteRepository loteRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionMapper notificacionMapper;


    @Override
    public Integer obtenerStockPorMedicamento(Long idMedicamento) {
        return loteRepository.sumStockByMedicamento(idMedicamento);
    }

    @Override
    public Integer obtenerStockPorSede(Long idSede) {
        return loteRepository.findBySedeId(idSede).stream()
                .mapToInt(lote -> lote.getStockLote() != null ? lote.getStockLote() : 0)
                .sum();
    }

    @Override
    public List<Medicamento> listarMedicamentos() {
        return medicamentoRepository.findAll();
    }

    @Override
    public List<Sede> listarSedes() {
        return sedeRepository.findAll();
    }


    @Override
    public List<NotificacionResponseDTO> obtenerNotificacionesConNombres() {
        return notificacionRepository.findAll().stream()
            .sorted(Comparator.comparing(Notificacion::getIdNotificacion).reversed())
            .map(notif -> {
                Medicamento med = notif.getMedicamento();
                Sede sede = notif.getSede();
                return NotificacionResponseDTO.of(
                        notif.getIdNotificacion(),
                        notif.getMensaje(),
                        notif.getTipo() != null ? notif.getTipo().name() : null,
                        notif.getEstado() != null ? notif.getEstado().name() : null,
                        notif.getFecha(),
                        med.getNombre(),
                        sede.getNombre()
                );
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Notificacion> listarNotificaciones() {
        return notificacionRepository.findAll();
    }

    @Override
    public Notificacion obtenerNotificacionPorId(Long idNotificacion) {
        return notificacionRepository.findById(idNotificacion).orElse(null);
    }

    public List<NotificacionResponseDTO> listarNotificacionesPorSede(Long idSede) {
        return notificacionRepository.findBySedeIdSedeOrderByFechaDesc(idSede).stream()
                .map(notificacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public Orden generarOrdenDesdeNotificacion(Long idGestor, Long idNotificacion, Integer cantidadSolicitada) {
        if (cantidadSolicitada == null || cantidadSolicitada <= 0) {
            throw new RuntimeException("La cantidad solicitada debe ser mayor a 0");
        }

        Notificacion notif = notificacionRepository.findById(idNotificacion)
            .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.NOTIFICACION_NO_ENCONTRADA));

        Usuario usuario = usuarioRepository.findById(idGestor)
            .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.USUARIO_NO_ENCONTRADO));

        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setSede(notif.getSede());
        orden.setTipo(com.proyecto.core.orden.domain.model.TipoOrden.COMPRA);
        orden.setEstado(com.proyecto.core.orden.domain.model.EstadoOrden.PENDIENTE);
        orden.setFecha(LocalDateTime.now());
        orden = ordenRepository.save(orden);

        DetalleOrden detalle = new DetalleOrden();
        detalle.setOrden(orden);
        detalle.setMedicamento(notif.getMedicamento());
        detalle.setCantidad(cantidadSolicitada);
        detalle.setEstado(com.proyecto.core.orden.domain.model.EstadoDetalle.PENDIENTE);
        detalleOrdenRepository.save(detalle);

        notif.setEstado(com.proyecto.core.notificacion.domain.model.EstadoNotificacion.RESUELTA);
        notificacionRepository.save(notif);

        return orden;
    }

    @Override
    public List<Orden> listarOrdenesPorGestor(Long idGestor) {
        return ordenRepository.findByUsuarioIdUsuario(idGestor);
    }

    @Override
    public List<DetalleOrden> obtenerDetallesDeOrden(Long idOrden) {
        return detalleOrdenRepository.findByOrdenIdOrden(idOrden);
    }

    @Override
    @Transactional
    public void eliminarOrden(Long idOrden) {
        detalleOrdenRepository.deleteByOrdenIdOrden(idOrden);
        ordenRepository.deleteById(idOrden);
    }

    @Override
    public List<StockPorSedeDto> obtenerStockPorSedeParaReporte() {
        return sedeRepository.findAll().stream()
                .map(sede -> new StockPorSedeDto(
                        sede.getIdSede(),
                        sede.getNombre(),
                        obtenerStockPorSede(sede.getIdSede())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockPorMedicamentoDto> obtenerStockPorMedicamentoParaReporte() {
        return medicamentoSedeRepository.findAll().stream()
                .map(ms -> new StockPorMedicamentoDto(
                        ms.getMedicamento().getIdMedicamento(),
                        ms.getMedicamento().getNombre(),
                        ms.getSede().getNombre(),
                        ms.getStockTotal()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<com.proyecto.core.notificacion.application.dto.NotificacionDto> obtenerNotificacionesConNombresParaPdf() {
        return notificacionRepository.findAll().stream()
            .map(notificacionMapper::toDto)
            .collect(Collectors.toList());
    }

    public String obtenerNombreMedicamento(Long idMedicamento) {
        return medicamentoRepository.findById(idMedicamento)
                .map(Medicamento::getNombre)
                .orElse("Medicamento no encontrado");
    }

    @Override
    public String obtenerNombreSede(Long idSede) {
        return sedeRepository.findById(idSede)
                .map(Sede::getNombre)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.SEDE_NO_ENCONTRADA));
    }

    @Override
    public List<Notificacion> obtenerNotificacionesParaReporte() {
        return notificacionRepository.findAll();
    }

    @Override
    public List<Orden> obtenerOrdenesParaReporte(Long idGestor) {
        return ordenRepository.findByUsuarioIdUsuario(idGestor);
    }

    // ========== OTROS ==========

    @Override
    public String obtenerNombreUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        return usuario != null ? usuario.getNombre() : "Usuario no encontrado";
    }

    @Override
    public List<Medicamento> listarMedicamentosBajoStock(Long idSede) {
        return medicamentoSedeRepository
                .findMedicamentosBajoStock(idSede, FarmaceuticoConstants.UMBRAL_BAJO_STOCK).stream()
                .map(MedicamentoSede::getMedicamento)
                .collect(Collectors.toList());
    }
}
