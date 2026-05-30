package com.proyecto.core.orden.infrastructure.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.core.orden.application.dto.OrdenTransportistaDTO;
import com.proyecto.core.orden.application.service.TransportistaService;

@RestController
@RequestMapping("/api/transportista/ordenes")
public class TransportistaRestController {

    private final TransportistaService service;

    public TransportistaRestController(TransportistaService service) {
        this.service = service;
    }

    // 🔹 LISTAR ÓRDENES (con filtros opcionales)
    @GetMapping
    public ResponseEntity<List<OrdenTransportistaDTO>> listarOrdenes(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String sede) {

        if (estado != null && estado.isEmpty()) estado = null;
        if (sede != null && sede.isEmpty()) sede = null;

        List<OrdenTransportistaDTO> ordenes = service.listarOrdenes(estado, sede);

        if (ordenes.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok(ordenes); // 200
    }

    // 🔹 OBTENER DETALLE DE UNA ORDEN
    @GetMapping("/{idDetalle}")
    public ResponseEntity<OrdenTransportistaDTO> obtenerDetalle(
            @PathVariable Long idDetalle) {

        OrdenTransportistaDTO dto = service.obtenerDetalle(idDetalle);

        if (dto == null) {
            return ResponseEntity.notFound().build(); // 404
        }

        return ResponseEntity.ok(dto); // 200
    }

    // 🔹 AVANZAR ESTADO DE LA ORDEN
    @PutMapping("/{idDetalle}/avanzar")
    public ResponseEntity<Void> avanzarEstado(
            @PathVariable Long idDetalle) {

        OrdenTransportistaDTO dto = service.obtenerDetalle(idDetalle);

        if (dto == null) {
            return ResponseEntity.notFound().build(); // 404
        }

        service.avanzarEstado(idDetalle);

        return ResponseEntity.ok().build(); // 200
    }
}
