package com.proyecto.controller;

import com.proyecto.dto.StockMedicamentoResponseDTO;
import com.proyecto.model.Usuario;
import com.proyecto.repository.UsuarioRepository;
import com.proyecto.service.MedicamentoService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller  // ← CAMBIADO: De @RestController a @Controller
@RequestMapping("/farmaceutico/stock")
@RequiredArgsConstructor
public class StockController {
    
    private final MedicamentoService medicamentoService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
 
    private Long obtenerIdSedeTemporal() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                    new RuntimeException("Usuario no encontrado"));

        if (usuario.getSede() == null) {
            throw new RuntimeException("El usuario no tiene sede asignada");
        }

        return usuario.getSede().getIdSede();
    }
    
    // ========== VISTAS THYMELEAF ==========
    
    // VISTA PRINCIPAL: Stock general (HTML)
    @GetMapping
    public String verStockGeneral(Model model) {
        Long idSede = obtenerIdSedeTemporal();
        List<StockMedicamentoResponseDTO> stock = medicamentoService.listarStockPorSede(idSede);
        
        // Calcular estadísticas
        int totalMedicamentos = stock.size();
        int totalStock = stock.stream().mapToInt(StockMedicamentoResponseDTO::getStockTotal).sum();
        int bajoStock = (int) stock.stream().filter(s -> "BAJO".equals(s.getEstadoStock()) || "CRITICO".equals(s.getEstadoStock())).count();
        int lotesProximos = stock.stream().mapToInt(StockMedicamentoResponseDTO::getLotesProximosCaducar).sum();
        
        model.addAttribute("stock", stock);
        model.addAttribute("titulo", "Gestión de Stock de Medicamentos");
        model.addAttribute("totalMedicamentos", totalMedicamentos);
        model.addAttribute("totalStock", totalStock);
        model.addAttribute("bajoStock", bajoStock);
        model.addAttribute("lotesProximos", lotesProximos);
        model.addAttribute("sedeId", idSede);
        
        return "farmaceutico/stock/gestion";
    }
    
    // VISTA: Stock de un medicamento específico (HTML)
    @GetMapping("/medicamento/{idMedicamento}")
    public String verStockMedicamento(@PathVariable Long idMedicamento, Model model) {
        try {
            StockMedicamentoResponseDTO stock = medicamentoService.obtenerStockMedicamento(idMedicamento);
            
            model.addAttribute("stock", stock);
            model.addAttribute("titulo", "Stock: " + stock.getNombreMedicamento());
            model.addAttribute("modo", "detalle");
            
            return "farmaceutico/stock/detalle";
        } catch (Exception e) {
            return "redirect:/farmaceutico/stock";
        }
    }
    
    // VISTA: Filtrar por bajo stock (HTML)
    @GetMapping("/bajo-stock")
    public String filtrarBajoStock(Model model) {
        Long idSede = obtenerIdSedeTemporal();
        List<StockMedicamentoResponseDTO> stock = medicamentoService.listarStockPorSede(idSede);
        
        // Filtrar solo los que están en bajo stock o crítico
        List<StockMedicamentoResponseDTO> bajoStock = stock.stream()
                .filter(s -> "BAJO".equals(s.getEstadoStock()) || "CRITICO".equals(s.getEstadoStock()))
                .toList();
        
        model.addAttribute("stock", bajoStock);
        model.addAttribute("titulo", "Medicamentos con Bajo Stock");
        model.addAttribute("filtro", "bajo-stock");
        model.addAttribute("count", bajoStock.size());
        model.addAttribute("sedeId", idSede);
        
        return "farmaceutico/stock/gestion";
    }
    
    // VISTA: Filtrar por stock normal (HTML)
    @GetMapping("/stock-normal")
    public String filtrarStockNormal(Model model) {
        Long idSede = obtenerIdSedeTemporal();
        List<StockMedicamentoResponseDTO> stock = medicamentoService.listarStockPorSede(idSede);
        
        // Filtrar solo los que tienen stock normal
        List<StockMedicamentoResponseDTO> normalStock = stock.stream()
                .filter(s -> "NORMAL".equals(s.getEstadoStock()))
                .toList();
        
        model.addAttribute("stock", normalStock);
        model.addAttribute("titulo", "Medicamentos con Stock Normal");
        model.addAttribute("filtro", "normal");
        model.addAttribute("count", normalStock.size());
        model.addAttribute("sedeId", idSede);
        
        return "farmaceutico/stock/gestion";
    }
    
    // VISTA: Solo estadísticas (HTML)
    @GetMapping("/estadisticas")
    public String obtenerEstadisticas(Model model) {
        Long idSede = obtenerIdSedeTemporal();
        List<StockMedicamentoResponseDTO> stock = medicamentoService.listarStockPorSede(idSede);
        
        int totalMedicamentos = stock.size();
        int totalStock = stock.stream().mapToInt(StockMedicamentoResponseDTO::getStockTotal).sum();
        int bajoStock = (int) stock.stream().filter(s -> "BAJO".equals(s.getEstadoStock())).count();
        int criticoStock = (int) stock.stream().filter(s -> "CRITICO".equals(s.getEstadoStock())).count();
        int normalStock = (int) stock.stream().filter(s -> "NORMAL".equals(s.getEstadoStock())).count();
        int lotesProximos = stock.stream().mapToInt(StockMedicamentoResponseDTO::getLotesProximosCaducar).sum();
        
        model.addAttribute("titulo", "Estadísticas de Stock");
        model.addAttribute("totalMedicamentos", totalMedicamentos);
        model.addAttribute("totalStock", totalStock);
        model.addAttribute("bajoStock", bajoStock);
        model.addAttribute("criticoStock", criticoStock);
        model.addAttribute("normalStock", normalStock);
        model.addAttribute("lotesProximos", lotesProximos);
        model.addAttribute("sedeId", idSede);
        
        return "farmaceutico/stock/estadisticas";
    }
    
    // ========== API JSON (para Postman) ==========
    
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> verStockGeneralApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<StockMedicamentoResponseDTO> stock = medicamentoService.listarStockPorSede(idSede);
            
            // Calcular estadísticas
            int totalMedicamentos = stock.size();
            int totalStock = stock.stream().mapToInt(StockMedicamentoResponseDTO::getStockTotal).sum();
            int bajoStock = (int) stock.stream().filter(s -> "BAJO".equals(s.getEstadoStock()) || "CRITICO".equals(s.getEstadoStock())).count();
            int lotesProximos = stock.stream().mapToInt(StockMedicamentoResponseDTO::getLotesProximosCaducar).sum();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stock);
            response.put("count", totalMedicamentos);
            response.put("estadisticas", Map.of(
                "totalStock", totalStock,
                "medicamentosBajoStock", bajoStock,
                "lotesProximosCaducar", lotesProximos,
                "sedeId", idSede
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/api/medicamento/{idMedicamento}")
    @ResponseBody
    public ResponseEntity<?> verStockMedicamentoApi(@PathVariable Long idMedicamento) {
        try {
            StockMedicamentoResponseDTO stock = medicamentoService.obtenerStockMedicamento(idMedicamento);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stock);
            response.put("mensaje", "Stock del medicamento");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/api/bajo-stock")
    @ResponseBody
    public ResponseEntity<?> filtrarBajoStockApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<StockMedicamentoResponseDTO> stock = medicamentoService.listarStockPorSede(idSede);
            
            // Filtrar solo los que están en bajo stock o crítico
            List<StockMedicamentoResponseDTO> bajoStock = stock.stream()
                    .filter(s -> "BAJO".equals(s.getEstadoStock()) || "CRITICO".equals(s.getEstadoStock()))
                    .toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", bajoStock);
            response.put("count", bajoStock.size());
            response.put("tipo", "BAJO_STOCK");
            response.put("sedeId", idSede);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/api/stock-normal")
    @ResponseBody
    public ResponseEntity<?> filtrarStockNormalApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<StockMedicamentoResponseDTO> stock = medicamentoService.listarStockPorSede(idSede);
            
            // Filtrar solo los que tienen stock normal
            List<StockMedicamentoResponseDTO> normalStock = stock.stream()
                    .filter(s -> "NORMAL".equals(s.getEstadoStock()))
                    .toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", normalStock);
            response.put("count", normalStock.size());
            response.put("tipo", "NORMAL_STOCK");
            response.put("sedeId", idSede);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/api/estadisticas")
    @ResponseBody
    public ResponseEntity<?> obtenerEstadisticasApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<StockMedicamentoResponseDTO> stock = medicamentoService.listarStockPorSede(idSede);
            
            int totalMedicamentos = stock.size();
            int totalStock = stock.stream().mapToInt(StockMedicamentoResponseDTO::getStockTotal).sum();
            int bajoStock = (int) stock.stream().filter(s -> "BAJO".equals(s.getEstadoStock())).count();
            int criticoStock = (int) stock.stream().filter(s -> "CRITICO".equals(s.getEstadoStock())).count();
            int normalStock = (int) stock.stream().filter(s -> "NORMAL".equals(s.getEstadoStock())).count();
            int lotesProximos = stock.stream().mapToInt(StockMedicamentoResponseDTO::getLotesProximosCaducar).sum();
            
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalMedicamentos", totalMedicamentos);
            estadisticas.put("totalStock", totalStock);
            estadisticas.put("medicamentosBajoStock", bajoStock);
            estadisticas.put("medicamentosCritico", criticoStock);
            estadisticas.put("medicamentosNormal", normalStock);
            estadisticas.put("lotesProximosCaducar", lotesProximos);
            estadisticas.put("sedeId", idSede);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "estadisticas", estadisticas,
                "mensaje", "Estadísticas de stock"
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Método auxiliar para respuestas de error
    private ResponseEntity<?> errorResponse(String message, HttpStatus status) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        error.put("status", status.value());
        
        return ResponseEntity.status(status).body(error);
    }
}