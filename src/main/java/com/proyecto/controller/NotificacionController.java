package com.proyecto.controller;

import com.proyecto.dto.NotificacionResponseDTO;
import com.proyecto.model.Usuario;
import com.proyecto.repository.UsuarioRepository;
import com.proyecto.service.NotificacionService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller  // ← CAMBIADO: De @RestController a @Controller
@RequestMapping("/farmaceutico/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {
    
    private final NotificacionService notificacionService;
    
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
    
    // VISTA: Listar todas las notificaciones (HTML)
    @GetMapping
    public String listarNotificaciones(Model model) {
        Long idSede = obtenerIdSedeTemporal();
        List<NotificacionResponseDTO> notificaciones = notificacionService.listarNotificacionesPorSede(idSede);
        
        model.addAttribute("notificaciones", notificaciones);
        model.addAttribute("titulo", "Notificaciones");
        model.addAttribute("sedeId", idSede);
        model.addAttribute("total", notificaciones.size());
        
        return "farmaceutico/notificaciones/lista";
    }
    
    // VISTA: Listar notificaciones PENDIENTES (HTML)
    @GetMapping("/pendientes")
    public String listarNotificacionesPendientes(Model model) {
        Long idSede = obtenerIdSedeTemporal();
        List<NotificacionResponseDTO> notificaciones = notificacionService.listarNotificacionesPendientes(idSede);
        
        model.addAttribute("notificaciones", notificaciones);
        model.addAttribute("titulo", "Notificaciones Pendientes");
        model.addAttribute("estado", "PENDIENTE");
        model.addAttribute("sedeId", idSede);
        model.addAttribute("total", notificaciones.size());
        
        return "farmaceutico/notificaciones/lista";
    }
    
    // VISTA: Listar notificaciones por TIPO (HTML)
    @GetMapping("/tipo/{tipo}")
    public String listarNotificacionesPorTipo(@PathVariable String tipo, Model model) {
        Long idSede = obtenerIdSedeTemporal();
        List<NotificacionResponseDTO> notificaciones = notificacionService.listarNotificacionesPorTipo(idSede, tipo);
        
        String tituloTipo = "BAJO_STOCK".equals(tipo) ? "Bajo Stock" : "Próxima Caducidad";
        
        model.addAttribute("notificaciones", notificaciones);
        model.addAttribute("titulo", "Notificaciones: " + tituloTipo);
        model.addAttribute("tipo", tipo);
        model.addAttribute("sedeId", idSede);
        model.addAttribute("total", notificaciones.size());
        
        return "farmaceutico/notificaciones/lista";
    }
    
    // VISTA: Detalle de notificación (HTML)
    @GetMapping("/{id}")
    public String obtenerNotificacion(@PathVariable Long id, Model model) {
        try {
            NotificacionResponseDTO notificacion = notificacionService.obtenerNotificacionPorId(id);
            
            model.addAttribute("notificacion", notificacion);
            model.addAttribute("titulo", "Detalle de Notificación");
            
            return "farmaceutico/notificaciones/detalle";
        } catch (Exception e) {
            return "redirect:/farmaceutico/notificaciones";
        }
    }
    
    // VISTA: Estadísticas de notificaciones (HTML)
    @GetMapping("/estadisticas")
    public String obtenerEstadisticasNotificaciones(Model model) {
        Long idSede = obtenerIdSedeTemporal();
        
        Long totalPendientes = notificacionService.contarNotificacionesPendientes(idSede);
        Long totalBajoStock = notificacionService.contarNotificacionesPorTipo(idSede, "BAJO_STOCK");
        Long totalProximoCaducar = notificacionService.contarNotificacionesPorTipo(idSede, "PROXIMO_CADUCAR");
        
        model.addAttribute("titulo", "Estadísticas de Notificaciones");
        model.addAttribute("pendientes", totalPendientes);
        model.addAttribute("bajoStock", totalBajoStock);
        model.addAttribute("proximoCaducar", totalProximoCaducar);
        model.addAttribute("total", totalPendientes + totalBajoStock + totalProximoCaducar);
        model.addAttribute("sedeId", idSede);
        
        return "farmaceutico/notificaciones/estadisticas";
    }
    
    // ACCIÓN: Generar notificaciones automáticas (HTML)
    @PostMapping("/generar-automaticas")
    public String generarNotificacionesAutomaticas(Model model, RedirectAttributes redirectAttributes) {
        try {
            Long idSede = obtenerIdSedeTemporal();
            notificacionService.generarNotificacionesAutomaticas(idSede);
            
            // Obtener las nuevas notificaciones generadas
            List<NotificacionResponseDTO> nuevas = notificacionService.listarNotificacionesPendientes(idSede);
            
            redirectAttributes.addFlashAttribute("success", 
                "Notificaciones automáticas generadas: " + nuevas.size() + " nuevas");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/farmaceutico/notificaciones";
    }
    
    // ========== API JSON (para Postman) ==========
    
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> listarNotificacionesApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<NotificacionResponseDTO> notificaciones = notificacionService.listarNotificacionesPorSede(idSede);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", notificaciones,
                "count", notificaciones.size(),
                "sedeId", idSede
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/api/pendientes")
    @ResponseBody
    public ResponseEntity<?> listarNotificacionesPendientesApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<NotificacionResponseDTO> notificaciones = notificacionService.listarNotificacionesPendientes(idSede);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", notificaciones,
                "count", notificaciones.size(),
                "estado", "PENDIENTE",
                "sedeId", idSede
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/api/tipo/{tipo}")
    @ResponseBody
    public ResponseEntity<?> listarNotificacionesPorTipoApi(@PathVariable String tipo) {
        try {
            Long idSede = obtenerIdSedeTemporal();
            List<NotificacionResponseDTO> notificaciones = notificacionService.listarNotificacionesPorTipo(idSede, tipo);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", notificaciones,
                "count", notificaciones.size(),
                "tipo", tipo,
                "sedeId", idSede
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerNotificacionApi(@PathVariable Long id) {
        try {
            NotificacionResponseDTO notificacion = notificacionService.obtenerNotificacionPorId(id);
            return ResponseEntity.ok(Map.of("success", true, "data", notificacion));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/api/contar/pendientes")
    @ResponseBody
    public ResponseEntity<?> contarNotificacionesPendientesApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            Long count = notificacionService.contarNotificacionesPendientes(idSede);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "count", count,
                "estado", "PENDIENTE",
                "sedeId", idSede,
                "mensaje", "Notificaciones pendientes"
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/api/contar/tipo/{tipo}")
    @ResponseBody
    public ResponseEntity<?> contarNotificacionesPorTipoApi(@PathVariable String tipo) {
        try {
            Long idSede = obtenerIdSedeTemporal();
            Long count = notificacionService.contarNotificacionesPorTipo(idSede, tipo);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "count", count,
                "tipo", tipo,
                "sedeId", idSede,
                "mensaje", "Notificaciones del tipo " + tipo
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/api/generar-automaticas")
    @ResponseBody
    public ResponseEntity<?> generarNotificacionesAutomaticasApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            notificacionService.generarNotificacionesAutomaticas(idSede);
            
            // Obtener las nuevas notificaciones generadas
            List<NotificacionResponseDTO> nuevas = notificacionService.listarNotificacionesPendientes(idSede);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "mensaje", "Notificaciones automáticas generadas",
                "nuevasNotificaciones", nuevas.size(),
                "data", nuevas,
                "sedeId", idSede
            ));
        } catch (Exception e) {
            return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/api/estadisticas")
    @ResponseBody
    public ResponseEntity<?> obtenerEstadisticasNotificacionesApi() {
        try {
            Long idSede = obtenerIdSedeTemporal();
            
            Long totalPendientes = notificacionService.contarNotificacionesPendientes(idSede);
            Long totalBajoStock = notificacionService.contarNotificacionesPorTipo(idSede, "BAJO_STOCK");
            Long totalProximoCaducar = notificacionService.contarNotificacionesPorTipo(idSede, "PROXIMO_CADUCAR");
            
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("pendientes", totalPendientes);
            estadisticas.put("bajoStock", totalBajoStock);
            estadisticas.put("proximoCaducar", totalProximoCaducar);
            estadisticas.put("total", totalPendientes + totalBajoStock + totalProximoCaducar);
            estadisticas.put("sedeId", idSede);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "estadisticas", estadisticas,
                "mensaje", "Estadísticas de notificaciones"
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