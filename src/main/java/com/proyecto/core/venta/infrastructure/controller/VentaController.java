package com.proyecto.core.venta.infrastructure.controller;

import com.proyecto.core.venta.application.dto.VentaRequestDTO;
import com.proyecto.core.venta.application.dto.VentaResponseDTO;
import com.proyecto.core.venta.application.service.VentaService;
import com.proyecto.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('FARMACEUTICO', 'ADMIN')")
    public ResponseEntity<ApiResponse<VentaResponseDTO>> procesarVenta(@Valid @RequestBody VentaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Venta completada", ventaService.procesarVenta(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FARMACEUTICO', 'GESTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<VentaResponseDTO>>> listarVentas() {
        return ResponseEntity.ok(ApiResponse.ok(ventaService.listarVentasPorSede()));
    }
}
