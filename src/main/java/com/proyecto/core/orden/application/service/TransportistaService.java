package com.proyecto.core.orden.application.service;

import java.util.List;
import com.proyecto.core.orden.application.dto.OrdenTransportistaDTO;

public interface TransportistaService {

	List<OrdenTransportistaDTO> listarOrdenes(String estado, String sede);

    OrdenTransportistaDTO obtenerDetalle(Long idDetalle);

    void avanzarEstado(Long idDetalle);
}
