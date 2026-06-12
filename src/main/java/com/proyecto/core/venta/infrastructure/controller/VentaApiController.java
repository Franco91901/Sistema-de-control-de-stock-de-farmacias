package com.proyecto.core.venta.infrastructure.controller;

import com.proyecto.core.venta.application.dto.MedicamentoDisponibleDTO;
import com.proyecto.core.venta.application.dto.VentaRequestDTO;
import com.proyecto.core.venta.application.dto.VentaResponseDTO;
import com.proyecto.core.venta.application.service.VentaService;
import com.proyecto.shared.dto.ApiResponse;
import com.proyecto.shared.security.AuthContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaApiController {

    private final VentaService ventaService;
    private final AuthContext authContext;

    @GetMapping("/medicamentos-disponibles")
    public ResponseEntity<ApiResponse<List<MedicamentoDisponibleDTO>>> medicamentosDisponibles() {
        return ResponseEntity.ok(ApiResponse.ok(
                ventaService.listarMedicamentosDisponibles(authContext.getIdSede())));
    }

    @PostMapping("/realizar")
    public ResponseEntity<ApiResponse<VentaResponseDTO>> realizarVenta(
            @Valid @RequestBody VentaRequestDTO request) {
        VentaResponseDTO venta = ventaService.realizarVenta(
                request, authContext.getIdUsuario(), authContext.getIdSede());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Venta realizada exitosamente", venta));
    }

    @GetMapping("/historial")
    public ResponseEntity<ApiResponse<List<VentaResponseDTO>>> historial() {
        return ResponseEntity.ok(ApiResponse.ok(
                ventaService.listarVentasPorSede(authContext.getIdSede())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VentaResponseDTO>> obtenerVenta(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(ventaService.obtenerVentaPorId(id)));
    }
}
