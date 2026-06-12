package com.proyecto.core.venta.application.service;

import com.proyecto.core.venta.application.dto.MedicamentoDisponibleDTO;
import com.proyecto.core.venta.application.dto.VentaRequestDTO;
import com.proyecto.core.venta.application.dto.VentaResponseDTO;

import java.util.List;

public interface VentaService {

    List<MedicamentoDisponibleDTO> listarMedicamentosDisponibles(Long idSede);

    VentaResponseDTO realizarVenta(VentaRequestDTO request, Long idUsuario, Long idSede);

    List<VentaResponseDTO> listarVentasPorUsuario(Long idUsuario);

    List<VentaResponseDTO> listarVentasPorSede(Long idSede);

    VentaResponseDTO obtenerVentaPorId(Long idVenta);
}
