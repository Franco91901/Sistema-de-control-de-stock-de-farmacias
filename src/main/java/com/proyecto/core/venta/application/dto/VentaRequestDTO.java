package com.proyecto.core.venta.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record VentaRequestDTO(
    @NotEmpty(message = "Debe incluir al menos un item")
    @Valid
    List<ItemVentaDTO> items
) {}
