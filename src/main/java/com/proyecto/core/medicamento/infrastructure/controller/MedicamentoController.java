package com.proyecto.core.medicamento.infrastructure.controller;

import com.proyecto.core.medicamento.application.dto.MedicamentoRequestDTO;
import com.proyecto.core.medicamento.application.dto.MedicamentoResponseDTO;
import com.proyecto.core.medicamento.application.service.MedicamentoService;
import com.proyecto.shared.dto.ApiResponse;
import com.proyecto.shared.security.AuthContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
@RequiredArgsConstructor
public class MedicamentoController {

    private final MedicamentoService medicamentoService;
    private final AuthContext authContext;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicamentoResponseDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(medicamentoService.listarMedicamentosPorSede(authContext.getIdSede())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicamentoResponseDTO>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(medicamentoService.obtenerMedicamentoPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MedicamentoResponseDTO>> crear(@Valid @RequestBody MedicamentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Medicamento creado", medicamentoService.crearMedicamento(dto, authContext.getIdSede())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicamentoResponseDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody MedicamentoRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(medicamentoService.actualizarMedicamento(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        medicamentoService.eliminarMedicamento(id);
        return ResponseEntity.ok(ApiResponse.ok("Medicamento eliminado", null));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<MedicamentoResponseDTO>>> buscar(@RequestParam String nombre) {
        return ResponseEntity.ok(ApiResponse.ok(
                medicamentoService.buscarMedicamentosPorNombre(nombre, authContext.getIdSede())));
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<ApiResponse<List<MedicamentoResponseDTO>>> bajoStock() {
        return ResponseEntity.ok(ApiResponse.ok(medicamentoService.buscarMedicamentosBajoStock(authContext.getIdSede())));
    }
}
