package com.proyecto.core.stock.infrastructure.controller;

import com.proyecto.core.medicamento.application.service.MedicamentoService;
import com.proyecto.core.stock.application.dto.StockEstadisticasDTO;
import com.proyecto.core.stock.application.dto.StockMedicamentoResponseDTO;
import com.proyecto.shared.dto.ApiResponse;
import com.proyecto.shared.security.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final MedicamentoService medicamentoService;
    private final AuthContext authContext;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockMedicamentoResponseDTO>>> stockGeneral() {
        return ResponseEntity.ok(ApiResponse.ok(medicamentoService.listarStockPorSede(authContext.getIdSede())));
    }

    @GetMapping("/medicamento/{id}")
    public ResponseEntity<ApiResponse<StockMedicamentoResponseDTO>> stockMedicamento(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(medicamentoService.obtenerStockMedicamento(id)));
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<ApiResponse<List<StockMedicamentoResponseDTO>>> bajoStock() {
        List<StockMedicamentoResponseDTO> bajo = medicamentoService.listarStockPorSede(authContext.getIdSede())
                .stream()
                .filter(s -> "BAJO".equals(s.estadoStock()) || "CRITICO".equals(s.estadoStock()))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(bajo));
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponse<StockEstadisticasDTO>> estadisticas() {
        List<StockMedicamentoResponseDTO> stock = medicamentoService.listarStockPorSede(authContext.getIdSede());
        return ResponseEntity.ok(ApiResponse.ok(new StockEstadisticasDTO(
            stock.size(),
            stock.stream().mapToInt(StockMedicamentoResponseDTO::stockTotal).sum(),
            stock.stream().filter(s -> "NORMAL".equals(s.estadoStock())).count(),
            stock.stream().filter(s -> "BAJO".equals(s.estadoStock())).count(),
            stock.stream().filter(s -> "CRITICO".equals(s.estadoStock())).count(),
            stock.stream().mapToInt(StockMedicamentoResponseDTO::lotesProximosCaducar).sum()
        )));
    }
}
