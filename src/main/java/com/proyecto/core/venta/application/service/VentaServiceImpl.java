package com.proyecto.core.venta.application.service;

import com.proyecto.core.lote.domain.model.Lote;
import com.proyecto.core.lote.domain.repository.LoteRepository;
import com.proyecto.core.medicamento.domain.model.Medicamento;
import com.proyecto.auth.domain.model.Usuario;
import com.proyecto.auth.domain.repository.UsuarioRepository;
import com.proyecto.core.medicamento.domain.model.MedicamentoSede;
import com.proyecto.core.medicamento.domain.repository.MedicamentoRepository;
import com.proyecto.core.medicamento.domain.repository.MedicamentoSedeRepository;
import com.proyecto.core.notificacion.application.service.NotificacionService;
import com.proyecto.core.sede.domain.model.Sede;
import com.proyecto.core.sede.domain.repository.SedeRepository;
import com.proyecto.core.stock.application.service.FarmaceuticoConstants;
import com.proyecto.core.stock.domain.model.MovimientoStock;
import com.proyecto.core.stock.domain.model.TipoMovimiento;
import com.proyecto.core.stock.domain.repository.MovimientoStockRepository;
import com.proyecto.core.venta.application.dto.ItemVentaDTO;
import com.proyecto.core.venta.application.dto.MedicamentoDisponibleDTO;
import com.proyecto.core.venta.application.dto.VentaRequestDTO;
import com.proyecto.core.venta.application.dto.VentaResponseDTO;
import com.proyecto.core.venta.application.mapper.VentaMapper;
import com.proyecto.core.venta.domain.model.DetalleVenta;
import com.proyecto.core.venta.domain.model.Venta;
import com.proyecto.core.venta.domain.repository.DetalleVentaRepository;
import com.proyecto.core.venta.domain.repository.VentaRepository;
import com.proyecto.shared.exception.EntityNotFoundException;
import com.proyecto.shared.exception.ExceptionConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final MedicamentoSedeRepository medicamentoSedeRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final LoteRepository loteRepository;
    private final SedeRepository sedeRepository;
    private final MovimientoStockRepository movimientoStockRepository;
    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;
    private final VentaMapper ventaMapper;

    @Override
    public List<MedicamentoDisponibleDTO> listarMedicamentosDisponibles(Long idSede) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(FarmaceuticoConstants.DIAS_ALERTA_CADUCIDAD);

        return medicamentoSedeRepository.findBySedeIdSede(idSede).stream()
                .filter(ms -> ms.getStockTotal() > 0)
                .filter(ms -> {
                    Integer count = loteRepository.countLotesProximosCaducar(
                            ms.getMedicamento().getIdMedicamento(), idSede, hoy, fechaLimite);
                    return count == 0;
                })
                .map(ventaMapper::toMedicamentoDisponibleDTO)
                .toList();
    }

    @Override
    @Transactional
    public VentaResponseDTO realizarVenta(VentaRequestDTO request, Long idUsuario, Long idSede) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.USUARIO_NO_ENCONTRADO));
        Sede sede = sedeRepository.findById(idSede)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.SEDE_NO_ENCONTRADA));

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setSede(sede);
        venta.setFecha(java.time.LocalDateTime.now());
        venta.setTotal(BigDecimal.ZERO);
        venta = ventaRepository.save(venta);

        BigDecimal totalVenta = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();

        for (ItemVentaDTO item : request.items()) {
            MedicamentoSede medSede = medicamentoSedeRepository
                    .findByMedicamentoIdMedicamentoAndSedeIdSede(item.idMedicamento(), idSede)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "El medicamento no existe en esta sede: " + item.idMedicamento()));

            if (medSede.getStockTotal() < item.cantidad()) {
                throw new IllegalArgumentException(
                        "Stock insuficiente para " + medSede.getMedicamento().getNombre()
                        + ". Disponible: " + medSede.getStockTotal() + ", solicitado: " + item.cantidad());
            }

            validarSinLotesProximosCaducar(item.idMedicamento(), idSede);

            List<Lote> lotesValidos = loteRepository.findLotesValidosParaVenta(
                    item.idMedicamento(), idSede, LocalDate.now().plusDays(FarmaceuticoConstants.DIAS_ALERTA_CADUCIDAD));

            int cantidadPorDescontar = item.cantidad();
            for (Lote lote : lotesValidos) {
                if (cantidadPorDescontar <= 0) break;
                int disponible = lote.getStockLote();
                int descontar = Math.min(disponible, cantidadPorDescontar);
                lote.setStockLote(disponible - descontar);
                loteRepository.save(lote);
                cantidadPorDescontar -= descontar;
            }

            medSede.setStockTotal(medSede.getStockTotal() - item.cantidad());
            medicamentoSedeRepository.save(medSede);

            BigDecimal subtotal = medSede.getPrecio().multiply(BigDecimal.valueOf(item.cantidad()));
            totalVenta = totalVenta.add(subtotal);

            DetalleVenta detalle = new DetalleVenta(venta, medSede.getMedicamento(),
                    item.cantidad(), medSede.getPrecio());
            detalle = detalleVentaRepository.save(detalle);
            detalles.add(detalle);

            MovimientoStock movimiento = new MovimientoStock();
            movimiento.setMedicamento(medSede.getMedicamento());
            movimiento.setSede(sede);
            movimiento.setTipo(TipoMovimiento.SALIDA);
            movimiento.setCantidad(item.cantidad());
            movimiento.setFecha(java.time.LocalDateTime.now());
            movimiento.setObservacion("Venta #" + venta.getIdVenta());
            movimientoStockRepository.save(movimiento);

            notificacionService.verificarNotificacionBajoStock(medSede);
        }

        venta.setDetalles(detalles);
        venta.setTotal(totalVenta);
        venta = ventaRepository.save(venta);

        return ventaMapper.toResponseDTO(venta);
    }

    @Override
    public List<VentaResponseDTO> listarVentasPorUsuario(Long idUsuario) {
        return ventaRepository.findByUsuarioIdUsuarioOrderByFechaDesc(idUsuario).stream()
                .map(ventaMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<VentaResponseDTO> listarVentasPorSede(Long idSede) {
        return ventaRepository.findBySedeIdSedeOrderByFechaDesc(idSede).stream()
                .map(ventaMapper::toResponseDTO)
                .toList();
    }

    @Override
    public VentaResponseDTO obtenerVentaPorId(Long idVenta) {
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
        return ventaMapper.toResponseDTO(venta);
    }

    private void validarSinLotesProximosCaducar(Long idMedicamento, Long idSede) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(FarmaceuticoConstants.DIAS_ALERTA_CADUCIDAD);
        Integer count = loteRepository.countLotesProximosCaducar(idMedicamento, idSede, hoy, fechaLimite);
        if (count > 0) {
            Medicamento med = medicamentoRepository.findById(idMedicamento)
                    .orElse(null);
            String nombre = med != null ? med.getNombre() : "ID " + idMedicamento;
            throw new IllegalArgumentException(
                    "El medicamento " + nombre + " tiene lotes próximos a caducar y no puede venderse");
        }
    }
}
