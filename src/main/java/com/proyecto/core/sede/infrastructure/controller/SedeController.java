package com.proyecto.core.sede.infrastructure.controller;

import com.proyecto.core.sede.application.dto.SedeDTO;
import com.proyecto.core.sede.application.service.SedeService;
import com.proyecto.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
@RequiredArgsConstructor
public class SedeController {

    private final SedeService sedeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SedeDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(sedeService.listarSedes()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SedeDTO>> obtener(@PathVariable Long id) {
        SedeDTO sede = sedeService.obtenerPorId(id);
        return sede != null
                ? ResponseEntity.ok(ApiResponse.ok(sede))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Sede no encontrada"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SedeDTO>> crear(@RequestBody SedeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Sede creada", sedeService.crearSede(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SedeDTO>> actualizar(@PathVariable Long id, @RequestBody SedeDTO dto) {
        SedeDTO actualizada = sedeService.actualizarSede(id, dto);
        return actualizada != null
                ? ResponseEntity.ok(ApiResponse.ok(actualizada))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Sede no encontrada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        try {
            sedeService.eliminarSede(id);
            return ResponseEntity.ok(ApiResponse.ok("Sede eliminada", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
