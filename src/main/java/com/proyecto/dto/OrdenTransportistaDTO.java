package com.proyecto.dto;

import lombok.Data;

@Data
public class OrdenTransportistaDTO {
	private Long idDetalle;
    private Long idOrden;
    private String medicamento;
    private String sede;
    private String direccionSede;
    private Integer cantidad;
    private String estado;
}
