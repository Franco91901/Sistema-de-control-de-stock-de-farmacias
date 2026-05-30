package com.proyecto.controller;

import com.proyecto.dto.LoteRequestDTO;
import com.proyecto.dto.LoteResponseDTO;
import com.proyecto.dto.LoteStockResponseDTO;
import com.proyecto.model.Usuario;
import com.proyecto.repository.UsuarioRepository;
import com.proyecto.service.LoteService;
import com.proyecto.service.MedicamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller  // ← CAMBIADO: De @RestController a @Controller
@RequestMapping("/farmaceutico/lotes")
@RequiredArgsConstructor
public class LoteController {
    
    private final LoteService loteService;
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
    
    // VISTA: Listar todos los lotes de la sede (HTML)
    @GetMapping
    public String listarLotes(Model model) {
        Long idSede = obtenerIdSedeTemporal();
        List<LoteResponseDTO> lotes = loteService.listarLotesPorSede(idSede);
        
        model.addAttribute("lotes", lotes);
        model.addAttribute("titulo", "Gestión de Lotes");
        model.addAttribute("sedeId", idSede);
        
        return "farmaceutico/lotes/lista";
    }
    
    // VISTA: Formulario crear lote (HTML)
    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        Long idSede = obtenerIdSedeTemporal();
        List<com.proyecto.dto.MedicamentoResponseDTO> medicamentos = medicamentoService.listarMedicamentosPorSede(idSede);
        
        model.addAttribute("loteRequest", new LoteRequestDTO());
        model.addAttribute("medicamentos", medicamentos);
        model.addAttribute("titulo", "Nuevo Lote");
        model.addAttribute("modo", "crear");
        
        return "farmaceutico/lotes/formulario";
    }
    
    // VISTA: Formulario editar lote (HTML)
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        try {
            LoteResponseDTO lote = loteService.obtenerLotePorId(id);
            LoteRequestDTO requestDTO = new LoteRequestDTO();
            requestDTO.setIdMedicamento(lote.getIdMedicamento());
            requestDTO.setCodigoLote(lote.getCodigoLote());
            requestDTO.setFechaCaducidad(lote.getFechaCaducidad());
            requestDTO.setStockLote(lote.getStockLote());
            
            Long idSede = obtenerIdSedeTemporal();
            List<com.proyecto.dto.MedicamentoResponseDTO> medicamentos = medicamentoService.listarMedicamentosPorSede(idSede);
            
            model.addAttribute("loteRequest", requestDTO);
            model.addAttribute("lote", lote);
            model.addAttribute("medicamentos", medicamentos);
            model.addAttribute("titulo", "Editar Lote");
            model.addAttribute("modo", "editar");
            
            return "farmaceutico/lotes/formulario";
        } catch (Exception e) {
            return "redirect:/farmaceutico/lotes";
        }
    }
    
    // ACCIÓN: Crear lote (POST desde formulario)
    @PostMapping("/crear")
    public String crearLote(
            @Valid @ModelAttribute("loteRequest") LoteRequestDTO requestDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        Long idSede = obtenerIdSedeTemporal();
        List<com.proyecto.dto.MedicamentoResponseDTO> medicamentos = medicamentoService.listarMedicamentosPorSede(idSede);
        
        if (result.hasErrors()) {
            model.addAttribute("medicamentos", medicamentos);
            model.addAttribute("titulo", "Nuevo Lote");
            model.addAttribute("modo", "crear");
            return "farmaceutico/lotes/formulario";
        }
        
        try {
            loteService.crearLote(requestDTO);
            redirectAttributes.addFlashAttribute("success", "Lote creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/farmaceutico/lotes";
    }
    
    // ACCIÓN: Actualizar lote (POST desde formulario)
    @PostMapping("/actualizar/{id}")
    public String actualizarLote(
            @PathVariable Long id,
            @Valid @ModelAttribute("loteRequest") LoteRequestDTO requestDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        Long idSede = obtenerIdSedeTemporal();
        List<com.proyecto.dto.MedicamentoResponseDTO> medicamentos = medicamentoService.listarMedicamentosPorSede(idSede);
        
        if (result.hasErrors()) {
            LoteResponseDTO lote = loteService.obtenerLotePorId(id);
            model.addAttribute("lote", lote);
            model.addAttribute("medicamentos", medicamentos);
            model.addAttribute("titulo", "Editar Lote");
            model.addAttribute("modo", "editar");
            return "farmaceutico/lotes/formulario";
        }
        
        try {
            loteService.actualizarLote(id, requestDTO);
            redirectAttributes.addFlashAttribute("success", "Lote actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/farmaceutico/lotes";
    }
    
    // ACCIÓN: Eliminar lote
    @GetMapping("/eliminar/{id}")
    public String eliminarLote(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            loteService.eliminarLote(id);
            redirectAttributes.addFlashAttribute("success", "Lote eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/farmaceutico/lotes";
    }
    
    // VISTA: Lotes por medicamento (HTML)
    @GetMapping("/medicamento/{idMedicamento}")
    public String listarLotesPorMedicamento(@PathVariable Long idMedicamento, Model model) {
        try {
            List<LoteResponseDTO> lotes = loteService.listarLotesPorMedicamento(idMedicamento);
            com.proyecto.dto.MedicamentoResponseDTO medicamento = medicamentoService.obtenerMedicamentoPorId(idMedicamento);
            
            model.addAttribute("lotes", lotes);
            model.addAttribute("medicamento", medicamento);
            model.addAttribute("titulo", "Lotes del Medicamento: " + medicamento.getNombre());
            
            return "farmaceutico/lotes/lista";
        } catch (Exception e) {
            return "redirect:/farmaceutico/lotes";
        }
    }
    
    // ACCIÓN: Retirar stock de un lote (HTML)
    @PostMapping("/{id}/retirar")
    public String retirarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad,
            RedirectAttributes redirectAttributes) {
        try {
            loteService.retirarStock(id, cantidad);
            redirectAttributes.addFlashAttribute("success", "Stock retirado exitosamente: " + cantidad + " unidades");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/farmaceutico/lotes";
    }
    
    // ACCIÓN: Retirar lotes vencidos (HTML)
    @PostMapping("/retirar-vencidos")
    public String retirarLotesVencidos(RedirectAttributes redirectAttributes) {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<LoteResponseDTO> lotesRetirados = loteService.retirarLotesVencidos(idSede);
            
            redirectAttributes.addFlashAttribute("success", 
                "Lotes vencidos retirados: " + lotesRetirados.size() + " lotes");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/farmaceutico/lotes";
    }
    
    // VISTA: Lotes para stock (HTML)
    @GetMapping("/stock")
    public String listarLotesStock(Model model) {
        Long idSede = obtenerIdSedeTemporal();
        List<LoteStockResponseDTO> lotes = loteService.listarLotesParaStock(idSede);
        
        model.addAttribute("lotes", lotes);
        model.addAttribute("titulo", "Stock de Lotes");
        model.addAttribute("sedeId", idSede);
        
        return "farmaceutico/lotes/stock";
    }
    
    // VISTA: Lotes próximos a caducar (HTML)
    @GetMapping("/proximos-caducar")
    public String listarLotesProximosCaducar(Model model) {
        Long idSede = obtenerIdSedeTemporal();
        List<LoteResponseDTO> lotes = loteService.listarLotesProximosCaducar(idSede);
        
        model.addAttribute("lotes", lotes);
        model.addAttribute("titulo", "Lotes Próximos a Caducar (≤30 días)");
        model.addAttribute("tipo", "proximos_caducar");
        
        return "farmaceutico/lotes/lista";
    }
    
    // VISTA: Lotes caducados (HTML)
    @GetMapping("/caducados")
    public String listarLotesCaducados(Model model) {
        Long idSede = obtenerIdSedeTemporal();
        List<LoteResponseDTO> lotes = loteService.listarLotesCaducados(idSede);
        
        model.addAttribute("lotes", lotes);
        model.addAttribute("titulo", "Lotes Caducados");
        model.addAttribute("tipo", "caducados");
        
        return "farmaceutico/lotes/lista";
    }
    
    // ========== API JSON (para Postman) ==========
    
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> listarLotesApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<LoteResponseDTO> lotes = loteService.listarLotesPorSede(idSede);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", lotes,
                "count", lotes.size(),
                "sedeId", idSede
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> crearLoteApi(@Valid @RequestBody LoteRequestDTO requestDTO) {
        try {
            LoteResponseDTO lote = loteService.crearLote(requestDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lote creado exitosamente");
            response.put("data", lote);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerLoteApi(@PathVariable Long id) {
        try {
            LoteResponseDTO lote = loteService.obtenerLotePorId(id);
            return ResponseEntity.ok(Map.of("success", true, "data", lote));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarLoteApi(
            @PathVariable Long id,
            @Valid @RequestBody LoteRequestDTO requestDTO) {
        try {
            LoteResponseDTO lote = loteService.actualizarLote(id, requestDTO);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Lote actualizado exitosamente",
                "data", lote
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarLoteApi(@PathVariable Long id) {
        try {
            loteService.eliminarLote(id);
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Lote eliminado exitosamente"
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/api/medicamento/{idMedicamento}")
    @ResponseBody
    public ResponseEntity<?> listarLotesPorMedicamentoApi(@PathVariable Long idMedicamento) {
        try {
            List<LoteResponseDTO> lotes = loteService.listarLotesPorMedicamento(idMedicamento);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", lotes,
                "count", lotes.size(),
                "idMedicamento", idMedicamento
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/api/{id}/retirar")
    @ResponseBody
    public ResponseEntity<?> retirarStockApi(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        try {
            LoteResponseDTO lote = loteService.retirarStock(id, cantidad);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Stock retirado exitosamente",
                "cantidadRetirada", cantidad,
                "data", lote
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/api/retirar-vencidos")
    @ResponseBody
    public ResponseEntity<?> retirarLotesVencidosApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<LoteResponseDTO> lotesRetirados = loteService.retirarLotesVencidos(idSede);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Lotes vencidos retirados exitosamente",
                "lotesRetirados", lotesRetirados,
                "count", lotesRetirados.size()
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/api/stock")
    @ResponseBody
    public ResponseEntity<?> listarLotesStockApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<LoteStockResponseDTO> lotes = loteService.listarLotesParaStock(idSede);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", lotes,
                "count", lotes.size()
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/api/proximos-caducar")
    @ResponseBody
    public ResponseEntity<?> listarLotesProximosCaducarApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<LoteResponseDTO> lotes = loteService.listarLotesProximosCaducar(idSede);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", lotes,
                "count", lotes.size(),
                "tipo", "PROXIMOS_CADUCAR"
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/api/caducados")
    @ResponseBody
    public ResponseEntity<?> listarLotesCaducadosApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<LoteResponseDTO> lotes = loteService.listarLotesCaducados(idSede);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", lotes,
                "count", lotes.size(),
                "tipo", "CADUCADOS"
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