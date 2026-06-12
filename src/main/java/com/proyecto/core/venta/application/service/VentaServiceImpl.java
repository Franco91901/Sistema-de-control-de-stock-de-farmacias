package com.proyecto.core.venta.application.service;

import com.proyecto.auth.domain.model.Usuario;
import com.proyecto.core.lote.domain.model.Lote;
import com.proyecto.core.lote.domain.repository.LoteRepository;
import com.proyecto.core.medicamento.domain.model.Medicamento;
import com.proyecto.core.medicamento.domain.repository.MedicamentoRepository;
import com.proyecto.core.medicamento.domain.repository.MedicamentoSedeRepository;
import com.proyecto.core.sede.domain.model.Sede;
import com.proyecto.core.sede.domain.repository.SedeRepository;
import com.proyecto.core.stock.domain.model.MovimientoStock;
import com.proyecto.core.stock.domain.model.TipoMovimiento;
import com.proyecto.core.stock.domain.repository.MovimientoStockRepository;
import com.proyecto.core.venta.application.dto.DetalleVentaResponseDTO;
import com.proyecto.core.venta.application.dto.VentaItemRequestDTO;
import com.proyecto.core.venta.application.dto.VentaRequestDTO;
import com.proyecto.core.venta.application.dto.VentaResponseDTO;
import com.proyecto.core.venta.domain.model.DetalleVenta;
import com.proyecto.core.venta.domain.model.Venta;
import com.proyecto.core.venta.domain.repository.VentaRepository;
import com.proyecto.shared.exception.EntityNotFoundException;
import com.proyecto.shared.exception.ExceptionConstants;
import com.proyecto.shared.security.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final LoteRepository loteRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final MedicamentoSedeRepository medicamentoSedeRepository;
    private final MovimientoStockRepository movimientoStockRepository;
    private final SedeRepository sedeRepository;
    private final AuthContext authContext;

    @Override
    @Transactional
    public VentaResponseDTO procesarVenta(VentaRequestDTO request) {
        Long idSede = authContext.getIdSede();
        Usuario usuario = authContext.getUsuario();
        Sede sede = sedeRepository.findById(idSede)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.SEDE_NO_ENCONTRADA));

        // Validación previa de stock para todos los ítems (fail-fast)
        for (VentaItemRequestDTO item : request.items()) {
            Integer stock = loteRepository.sumStockByMedicamentoAndSede(item.idMedicamento(), idSede);
            if (stock == null || stock < item.cantidad()) {
                Medicamento med = medicamentoRepository.findById(item.idMedicamento())
                        .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.MEDICAMENTO_NO_ENCONTRADO));
                throw new IllegalStateException(
                        "Stock insuficiente para '" + med.getNombre() + "'. Disponible: " + (stock != null ? stock : 0));
            }
        }

        Venta venta = new Venta();
        venta.setSede(sede);
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();

        for (VentaItemRequestDTO item : request.items()) {
            Medicamento medicamento = medicamentoRepository.findById(item.idMedicamento())
                    .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.MEDICAMENTO_NO_ENCONTRADO));

            // Descuento FIFO: lotes ordenados por fecha de caducidad más próxima
            List<Lote> lotesFIFO = loteRepository.findLotesDisponiblesFIFO(item.idMedicamento(), idSede);
            int restante = item.cantidad();

            for (Lote lote : lotesFIFO) {
                if (restante <= 0) break;
                int aDeducir = Math.min(restante, lote.getStockLote());
                lote.setStockLote(lote.getStockLote() - aDeducir);
                loteRepository.save(lote);
                registrarMovimiento(medicamento, sede, lote, TipoMovimiento.SALIDA, aDeducir,
                        "Venta - lote: " + lote.getCodigoLote());
                restante -= aDeducir;
            }

            // Actualizar stock consolidado en MedicamentoSede
            Integer nuevoStock = loteRepository.sumStockByMedicamentoAndSede(item.idMedicamento(), idSede);
            medicamentoSedeRepository.findByMedicamentoIdMedicamentoAndSedeIdSede(item.idMedicamento(), idSede)
                    .ifPresent(ms -> {
                        ms.setStockTotal(nuevoStock != null ? nuevoStock : 0);
                        medicamentoSedeRepository.save(ms);
                    });

            BigDecimal subtotal = item.precioUnitario().multiply(BigDecimal.valueOf(item.cantidad()));
            total = total.add(subtotal);

            DetalleVenta detalle = new DetalleVenta();
            detalle.setMedicamento(medicamento);
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(item.precioUnitario());
            detalle.setSubtotal(subtotal);
            detalles.add(detalle);
        }

        venta.setTotal(total);
        detalles.forEach(d -> d.setVenta(venta));
        venta.setDetalles(detalles);
        Venta saved = ventaRepository.save(venta);

        return toResponseDTO(saved);
    }

    @Override
    public List<VentaResponseDTO> listarVentasPorSede() {
        Long idSede = authContext.getIdSede();
        return ventaRepository.findBySedeIdSedeOrderByFechaDesc(idSede)
                .stream().map(this::toResponseDTO).toList();
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

    private VentaResponseDTO toResponseDTO(Venta venta) {
        String nombreUsuario = "";
        if (venta.getUsuario() != null) {
            nombreUsuario = venta.getUsuario().getNombre();
            if (venta.getUsuario().getApellido() != null) {
                nombreUsuario += " " + venta.getUsuario().getApellido();
            }
        }

        List<DetalleVentaResponseDTO> detallesDTO = venta.getDetalles().stream()
                .map(d -> new DetalleVentaResponseDTO(
                        d.getIdDetalle(),
                        d.getMedicamento() != null ? d.getMedicamento().getNombre() : "",
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getSubtotal()
                )).toList();

        return new VentaResponseDTO(
                venta.getIdVenta(),
                venta.getFecha(),
                venta.getTotal(),
                venta.getSede() != null ? venta.getSede().getNombre() : "",
                nombreUsuario.trim(),
                detallesDTO
        );
    }
}
