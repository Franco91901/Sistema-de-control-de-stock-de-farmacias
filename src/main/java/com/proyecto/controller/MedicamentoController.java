package com.proyecto.controller;

import com.proyecto.dto.MedicamentoRequestDTO;
import com.proyecto.dto.MedicamentoResponseDTO;
import com.proyecto.model.Usuario;
import com.proyecto.repository.UsuarioRepository;
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
@RequestMapping("/farmaceutico/medicamentos")
@RequiredArgsConstructor
public class MedicamentoController {
    
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
    
    // VISTA: Listar medicamentos (HTML)
    @GetMapping
    public String listarMedicamentos(Model model) {
        Long idSede = obtenerIdSedeTemporal();
        List<MedicamentoResponseDTO> medicamentos = medicamentoService.listarMedicamentosPorSede(idSede);
        
        model.addAttribute("medicamentos", medicamentos);
        model.addAttribute("titulo", "Gestión de Medicamentos");
        model.addAttribute("sedeId", idSede);
        
        return "farmaceutico/medicamentos/lista";
    }
    
    // VISTA: Formulario crear medicamento (HTML)
    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("medicamentoRequest", new MedicamentoRequestDTO());
        model.addAttribute("titulo", "Nuevo Medicamento");
        model.addAttribute("sedeId", obtenerIdSedeTemporal());
        model.addAttribute("modo", "crear");
 
        
        return "farmaceutico/medicamentos/formulario";
    }
    
    // VISTA: Formulario editar medicamento (HTML)
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        try {
            MedicamentoResponseDTO medicamento = medicamentoService.obtenerMedicamentoPorId(id);
            MedicamentoRequestDTO requestDTO = new MedicamentoRequestDTO();
            requestDTO.setNombre(medicamento.getNombre());
            requestDTO.setDescripcion(medicamento.getDescripcion());
            requestDTO.setIdSede(medicamento.getIdSede());
            
            model.addAttribute("medicamentoRequest", requestDTO);
            model.addAttribute("medicamento", medicamento);
            model.addAttribute("titulo", "Editar Medicamento");
            model.addAttribute("modo", "editar");
            
            return "farmaceutico/medicamentos/formulario";
        } catch (Exception e) {
            return "redirect:/farmaceutico/medicamentos";
        }
    }
    
    // ACCIÓN: Crear medicamento (POST desde formulario)
    @PostMapping("/crear")
    public String crearMedicamento(
            @Valid @ModelAttribute("medicamentoRequest") MedicamentoRequestDTO requestDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Nuevo Medicamento");
            model.addAttribute("sedeId", obtenerIdSedeTemporal());
            model.addAttribute("modo", "crear");
            return "farmaceutico/medicamentos/formulario";
        }
        
        try {
            Long idSede = obtenerIdSedeTemporal();
            medicamentoService.crearMedicamento(requestDTO, idSede);
            redirectAttributes.addFlashAttribute("success", "Medicamento creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/farmaceutico/medicamentos";
    }
    
    // ACCIÓN: Actualizar medicamento (POST desde formulario)
    @PostMapping("/actualizar/{id}")
    public String actualizarMedicamento(
            @PathVariable Long id,
            @Valid @ModelAttribute("medicamentoRequest") MedicamentoRequestDTO requestDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            MedicamentoResponseDTO medicamento = medicamentoService.obtenerMedicamentoPorId(id);
            model.addAttribute("medicamento", medicamento);
            model.addAttribute("titulo", "Editar Medicamento");
            model.addAttribute("modo", "editar");
            return "farmaceutico/medicamentos/formulario";
        }
        
        try {
            medicamentoService.actualizarMedicamento(id, requestDTO);
            redirectAttributes.addFlashAttribute("success", "Medicamento actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/farmaceutico/medicamentos";
    }
    
    // ACCIÓN: Eliminar medicamento
    @GetMapping("/eliminar/{id}")
    public String eliminarMedicamento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            medicamentoService.eliminarMedicamento(id);
            redirectAttributes.addFlashAttribute("success", "Medicamento eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/farmaceutico/medicamentos";
    }
    
    // VISTA: Buscar medicamentos (HTML)
    @GetMapping("/buscar")
    public String buscarMedicamentos(@RequestParam String nombre, Model model) {
        Long idSede = obtenerIdSedeTemporal();
        List<MedicamentoResponseDTO> medicamentos = medicamentoService
                .buscarMedicamentosPorNombre(nombre, idSede);
        
        model.addAttribute("medicamentos", medicamentos);
        model.addAttribute("titulo", "Resultados de búsqueda: " + nombre);
        model.addAttribute("sedeId", idSede);
        model.addAttribute("terminoBusqueda", nombre);
        
        return "farmaceutico/medicamentos/lista";
    }
    
    // ========== API JSON (para Postman) ==========
    
    @GetMapping("/api")
    @ResponseBody  // ← Esto hace que devuelva JSON
    public ResponseEntity<?> listarMedicamentosApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<MedicamentoResponseDTO> medicamentos = medicamentoService.listarMedicamentosPorSede(idSede);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", medicamentos);
            response.put("count", medicamentos.size());
            response.put("sedeId", idSede);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> crearMedicamentoApi(@Valid @RequestBody MedicamentoRequestDTO requestDTO) {
        try {
            Long idSede = obtenerIdSedeTemporal();
            MedicamentoResponseDTO medicamento = medicamentoService.crearMedicamento(requestDTO, idSede);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Medicamento creado exitosamente");
            response.put("data", medicamento);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerMedicamentoApi(@PathVariable Long id) {
        try {
            MedicamentoResponseDTO medicamento = medicamentoService.obtenerMedicamentoPorId(id);
            return ResponseEntity.ok(Map.of("success", true, "data", medicamento));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarMedicamentoApi(
            @PathVariable Long id,
            @Valid @RequestBody MedicamentoRequestDTO requestDTO) {
        try {
            MedicamentoResponseDTO medicamento = medicamentoService.actualizarMedicamento(id, requestDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Medicamento actualizado exitosamente");
            response.put("data", medicamento);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarMedicamentoApi(@PathVariable Long id) {
        try {
            medicamentoService.eliminarMedicamento(id);
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Medicamento eliminado exitosamente"
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/api/buscar")
    @ResponseBody
    public ResponseEntity<?> buscarMedicamentosApi(@RequestParam String nombre) {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<MedicamentoResponseDTO> medicamentos = medicamentoService
                    .buscarMedicamentosPorNombre(nombre, idSede);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", medicamentos,
                "count", medicamentos.size()
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