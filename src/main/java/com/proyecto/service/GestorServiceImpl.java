package com.proyecto.service;

import com.proyecto.dto.NotificacionResponseDTO;
import com.proyecto.dto.StockPorMedicamentoDto;
import com.proyecto.dto.StockPorSedeDto;
import com.proyecto.model.*;
import com.proyecto.mapper.NotificacionMapper;
import com.proyecto.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GestorServiceImpl implements GestorService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private NotificacionMapper notificacionMapper;

    // ========== STOCK ==========

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

    // ========== NOTIFICACIONES ==========
    
    @Override
    public List<NotificacionResponseDTO> obtenerNotificacionesConNombres() {
        return notificacionRepository.findAll().stream()
            .sorted(Comparator.comparing(Notificacion::getIdNotificacion).reversed())
            .map(notif -> {
                Medicamento med = notif.getMedicamento();
                Sede sede = notif.getSede();
                return new NotificacionResponseDTO(
                        notif.getIdNotificacion(),
                        notif.getMensaje(),
                        notif.getTipo(),
                        notif.getEstado(),
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
        return notificacionRepository.findById(idNotificacion)
                .orElse(null);
    }
    
    public List<NotificacionResponseDTO> listarNotificacionesPorSede(Long idSede) {
        List<Notificacion> notificaciones = notificacionRepository.findBySedeIdSedeOrderByFechaDesc(idSede);
        return notificaciones.stream()
                .map(notificacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ========== ÓRDENES ==========
        
    @Override
    @Transactional
    public Orden generarOrdenDesdeNotificacion(Long idGestor, Long idNotificacion, Integer cantidadSolicitada) {
        if (cantidadSolicitada == null || cantidadSolicitada <= 0) {
            throw new RuntimeException("La cantidad solicitada debe ser mayor a 0");
        }

        Notificacion notif = notificacionRepository.findById(idNotificacion)
            .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        Orden orden = new Orden();
        orden.setIdGestor(idGestor); 
        orden.setFecha(LocalDateTime.now());
        orden = ordenRepository.save(orden);

        DetalleOrden detalle = new DetalleOrden();
        detalle.setOrden(orden); 
        detalle.setMedicamento(notif.getMedicamento()); 
        detalle.setCantidad(cantidadSolicitada);
        detalle.setEstado("PENDIENTE");
        detalleOrdenRepository.save(detalle);

        notif.setEstado("RESUELTA");
        notificacionRepository.save(notif);

        return orden;
    }

    @Override
    public List<Orden> listarOrdenesPorGestor(Long idGestor) {
        return ordenRepository.findByIdGestor(idGestor.intValue());
    }
    
    @Override
    public List<DetalleOrden> obtenerDetallesDeOrden(Long idOrden) {
        return detalleOrdenRepository.findByOrdenIdOrden(idOrden);
    }
    
    @Override
    @Transactional
    public void eliminarOrden(Long idOrden) {
        detalleOrdenRepository.deleteById(idOrden);
        ordenRepository.deleteById(idOrden);
    }

    // ========== REPORTES (usar tus DTOs con Integer) ==========

    @Override
    public List<StockPorSedeDto> obtenerStockPorSedeParaReporte() {
        return sedeRepository.findAll().stream()
                .map(sede -> new StockPorSedeDto(
                        sede.getIdSede().intValue(),
                        sede.getNombre(),
                        obtenerStockPorSede(sede.getIdSede())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<StockPorMedicamentoDto> obtenerStockPorMedicamentoParaReporte() {
        return medicamentoRepository.findAll().stream()
                .map(med -> new StockPorMedicamentoDto(
                        med.getIdMedicamento().intValue(),
                        med.getNombre(),
                        med.getSede().getNombre(),
                        obtenerStockPorMedicamento(med.getIdMedicamento())
                ))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<com.proyecto.dto.NotificacionDto> obtenerNotificacionesConNombresParaPdf() {
        return notificacionRepository.findAll().stream()
            .map(notificacionMapper::toDto) // 👈 Usa el mapper corregido
            .collect(Collectors.toList());
    }
    
    public String obtenerNombreMedicamento(Long idMedicamento) {
        return medicamentoRepository.findById(idMedicamento)
                .map(Medicamento::getNombre)
                .orElse("Medicamento no encontrado");
    }

    @Override
    public String obtenerNombreSede(Long idSede) {
        Sede sede = sedeRepository.findById(idSede)
                .orElseThrow(() -> new IllegalArgumentException("Sede no encontrada"));
        return sede.getNombre();
    }
    

    @Override
    public List<Notificacion> obtenerNotificacionesParaReporte() {
        return notificacionRepository.findAll();
    }

    @Override
    public List<Orden> obtenerOrdenesParaReporte(Long idGestor) {
        return ordenRepository.findByIdGestor(idGestor.intValue());
    }

    // ========== OTROS ==========

    @Override
    public String obtenerNombreUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario.intValue()).orElse(null);
        return usuario != null ? usuario.getNombre() : "Usuario no encontrado";
    }
    
    @Override
    public List<Medicamento> listarMedicamentosBajoStock(Long idSede) {
        return medicamentoRepository.findMedicamentosBajoStock(idSede, FarmaceuticoConstants.UMBRAL_BAJO_STOCK);
    }
}