package com.proyecto.core.venta.application.service;

import com.proyecto.core.venta.application.dto.VentaRequestDTO;
import com.proyecto.core.venta.application.dto.VentaResponseDTO;

import java.util.List;

public interface VentaService {

    VentaResponseDTO procesarVenta(VentaRequestDTO request);

    List<VentaResponseDTO> listarVentasPorSede();
}
