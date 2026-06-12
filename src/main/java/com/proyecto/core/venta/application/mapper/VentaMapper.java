package com.proyecto.core.venta.application.mapper;

import com.proyecto.core.medicamento.domain.model.MedicamentoSede;
import com.proyecto.core.venta.application.dto.DetalleVentaResponseDTO;
import com.proyecto.core.venta.application.dto.MedicamentoDisponibleDTO;
import com.proyecto.core.venta.application.dto.VentaResponseDTO;
import com.proyecto.core.venta.domain.model.DetalleVenta;
import com.proyecto.core.venta.domain.model.Venta;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VentaMapper {

    public VentaResponseDTO toResponseDTO(Venta venta) {
        if (venta == null) return null;

        Long idUsuario = venta.getUsuario() != null ? venta.getUsuario().getIdUsuario() : null;
        String nombreUsuario = venta.getUsuario() != null ? venta.getUsuario().getNombre() : null;
        Long idSede = venta.getSede() != null ? venta.getSede().getIdSede() : null;
        String nombreSede = venta.getSede() != null ? venta.getSede().getNombre() : null;

        List<DetalleVentaResponseDTO> detallesDTO = venta.getDetalles().stream()
                .map(this::toDetalleResponseDTO)
                .toList();

        return new VentaResponseDTO(
            venta.getIdVenta(),
            idUsuario,
            nombreUsuario,
            idSede,
            nombreSede,
            venta.getFecha(),
            venta.getTotal(),
            detallesDTO.size(),
            detallesDTO
        );
    }

    public DetalleVentaResponseDTO toDetalleResponseDTO(DetalleVenta detalle) {
        if (detalle == null) return null;
        return new DetalleVentaResponseDTO(
            detalle.getIdDetalle(),
            detalle.getMedicamento().getIdMedicamento(),
            detalle.getMedicamento().getNombre(),
            detalle.getCantidad(),
            detalle.getPrecioUnitario(),
            detalle.getSubtotal()
        );
    }

    public MedicamentoDisponibleDTO toMedicamentoDisponibleDTO(MedicamentoSede ms) {
        if (ms == null) return null;
        return new MedicamentoDisponibleDTO(
            ms.getMedicamento().getIdMedicamento(),
            ms.getMedicamento().getNombre(),
            ms.getMedicamento().getDescripcion(),
            ms.getStockTotal(),
            ms.getPrecio()
        );
    }
}
