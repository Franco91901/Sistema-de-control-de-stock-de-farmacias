// com.proyecto.controller.DashboardController
package com.proyecto.controller;

import com.proyecto.dto.MenuOptionDTO;
import com.proyecto.repository.RolRepository;
import com.proyecto.repository.SedeRepository;
import com.proyecto.repository.UsuarioRepository;
import com.proyecto.service.*;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class DashboardController {

    private final MenuService menuService;
    private final NotificacionService notificacionService;
    private final GestorService gestorService;
    private final TransportistaService transportistaService;
    private final MedicamentoService medicamentoService;
    private final UsuarioService adminService;

    
    
    
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        // 1. Obtener rol (Spring Security agrega "ROLE_" por defecto)
        String userRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", "")) // Elimina "ROLE_"
                .findFirst()
                .orElse("ANONYMOUS");

        // 2. Menú dinámico
        List<MenuOptionDTO> menu = menuService.getMenuForUser(userRole);
        model.addAttribute("menuOptions", menu);
        model.addAttribute("userRole", userRole);

        // 3. Nombre del usuario (opcional)
        model.addAttribute("userName", authentication.getName());

        // 4. Datos según rol
        if ("FARMACEUTICO".equals(userRole) || "ADMINISTRADOR".equals(userRole)) {
            Long sedeId = 1L; // Temporal: Sede Central
            model.addAttribute("totalStock", gestorService.obtenerStockPorSede(sedeId));
            model.addAttribute("bajoStockCount", gestorService.listarMedicamentosBajoStock(sedeId).size());
            model.addAttribute("proximosCaducar", notificacionService.contarNotificacionesPorTipo(sedeId, "PROXIMO_CADUCAR"));
            model.addAttribute("notificacionesPendientes", notificacionService.contarNotificacionesPendientes(sedeId));
            
        } else if ("GESTOR".equals(userRole)) {
            Long sedeId = 1L;
            model.addAttribute("notificacionesPendientes", notificacionService.contarNotificacionesPendientes(sedeId));
            model.addAttribute("ordenesPendientes", gestorService.listarOrdenesPorGestor(3L).size()); // ID temporal
        } else if ("TRANSPORTISTA".equals(userRole)) {
            model.addAttribute("ordenesPendientes", transportistaService.listarOrdenes("PENDIENTE", null).size());
        }

        return "dashboard";
    }
}