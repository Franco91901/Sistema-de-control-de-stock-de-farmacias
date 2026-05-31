package com.proyecto.core.orden.infrastructure.controller;

import com.proyecto.core.orden.application.dto.OrdenTransportistaDTO;
import com.proyecto.core.orden.application.service.TransportistaService;
import com.proyecto.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportista/ordenes")
@RequiredArgsConstructor
public class TransportistaController {

    private final TransportistaService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrdenTransportistaDTO>>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String sede) {
        if (estado != null && estado.isEmpty()) estado = null;
        if (sede != null && sede.isEmpty()) sede = null;
        return ResponseEntity.ok(ApiResponse.ok(service.listarOrdenes(estado, sede)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrdenTransportistaDTO>> obtener(@PathVariable Long id) {
        OrdenTransportistaDTO dto = service.obtenerDetalle(id);
        return dto != null
                ? ResponseEntity.ok(ApiResponse.ok(dto))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Orden no encontrada"));
    }

    @PutMapping("/{id}/avanzar")
    public ResponseEntity<ApiResponse<Void>> avanzar(@PathVariable Long id) {
        OrdenTransportistaDTO dto = service.obtenerDetalle(id);
        if (dto == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Orden no encontrada"));
        service.avanzarEstado(id);
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado", null));
    }
}
