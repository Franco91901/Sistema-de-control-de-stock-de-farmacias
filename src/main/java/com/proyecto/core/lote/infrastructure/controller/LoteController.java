package com.proyecto.core.lote.infrastructure.controller;

import com.proyecto.core.lote.application.dto.LoteRequestDTO;
import com.proyecto.core.lote.application.dto.LoteResponseDTO;
import com.proyecto.core.lote.application.dto.LoteStockResponseDTO;
import com.proyecto.core.lote.application.service.LoteService;
import com.proyecto.shared.dto.ApiResponse;
import com.proyecto.shared.security.AuthContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lotes")
@RequiredArgsConstructor
public class LoteController {

    private final LoteService loteService;
    private final AuthContext authContext;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LoteResponseDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(loteService.listarLotesPorSede(authContext.getIdSede())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoteResponseDTO>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(loteService.obtenerLotePorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LoteResponseDTO>> crear(@Valid @RequestBody LoteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Lote creado", loteService.crearLote(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LoteResponseDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody LoteRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(loteService.actualizarLote(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        loteService.eliminarLote(id);
        return ResponseEntity.ok(ApiResponse.ok("Lote eliminado", null));
    }

    @GetMapping("/medicamento/{id}")
    public ResponseEntity<ApiResponse<List<LoteResponseDTO>>> porMedicamento(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(loteService.listarLotesPorMedicamento(id)));
    }

    @PostMapping("/{id}/retirar")
    public ResponseEntity<ApiResponse<LoteResponseDTO>> retirarStock(
            @PathVariable Long id, @RequestParam Integer cantidad) {
        return ResponseEntity.ok(ApiResponse.ok(loteService.retirarStock(id, cantidad)));
    }

    @PostMapping("/retirar-vencidos")
    public ResponseEntity<ApiResponse<List<LoteResponseDTO>>> retirarVencidos() {
        return ResponseEntity.ok(ApiResponse.ok(loteService.retirarLotesVencidos(authContext.getIdSede())));
    }

    @GetMapping("/stock")
    public ResponseEntity<ApiResponse<List<LoteStockResponseDTO>>> stock() {
        return ResponseEntity.ok(ApiResponse.ok(loteService.listarLotesParaStock(authContext.getIdSede())));
    }

    @GetMapping("/proximos-caducar")
    public ResponseEntity<ApiResponse<List<LoteResponseDTO>>> proximosCaducar() {
        return ResponseEntity.ok(ApiResponse.ok(loteService.listarLotesProximosCaducar(authContext.getIdSede())));
    }

    @GetMapping("/caducados")
    public ResponseEntity<ApiResponse<List<LoteResponseDTO>>> caducados() {
        return ResponseEntity.ok(ApiResponse.ok(loteService.listarLotesCaducados(authContext.getIdSede())));
    }
}
