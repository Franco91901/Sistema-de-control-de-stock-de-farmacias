// com.proyecto.auth.application.service.MenuService
package com.proyecto.auth.application.service;

import com.proyecto.auth.application.dto.MenuOptionDTO;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    public List<MenuOptionDTO> getMenuForUser(String userRole) {
        return buildFullMenu().stream()
                .filter(option -> option.allowedRoles().contains(userRole))
                .collect(Collectors.toList());
    }
    
 // com.proyecto.auth.application.service.MenuService
    private List<MenuOptionDTO> buildFullMenu() {
        return Arrays.asList(
            new MenuOptionDTO("Dashboard", "fa-home", "/dashboard", List.of("ADMINISTRADOR", "FARMACEUTICO", "GESTOR", "TRANSPORTISTA")),
            
            // 👇 Solo para ADMINISTRADOR y FARMACEUTICO
            new MenuOptionDTO("Usuarios", "fa-users", "/admin/usuarios",List.of("ADMINISTRADOR", "FARMACEUTICO")),
            new MenuOptionDTO("Sedes", "fa-users", "/admin/sedes",List.of("ADMINISTRADOR", "FARMACEUTICO")),
            new MenuOptionDTO("Medicamentos", "fa-capsules", "/farmaceutico/medicamentos", List.of("ADMINISTRADOR", "FARMACEUTICO")),
            new MenuOptionDTO("Stock y Lotes", "fa-boxes", "/farmaceutico/stock", List.of("ADMINISTRADOR", "FARMACEUTICO")),
            
            // 👇 Para ADMINISTRADOR, FARMACEUTICO y GESTOR
            new MenuOptionDTO("Notificaciones", "fa-bell", "/gestor/notificaciones", List.of("ADMINISTRADOR", "FARMACEUTICO", "GESTOR")),
            
            // 👇 Para ADMINISTRADOR, GESTOR y TRANSPORTISTA
            new MenuOptionDTO("Órdenes", "fa-truck", "/gestor/ordenes", List.of("ADMINISTRADOR", "GESTOR", "TRANSPORTISTA")),
            
            // 👇 Para GESTOR (reportes)
            new MenuOptionDTO("Reportes", "fa-file-alt", "/gestor/stock", List.of("GESTOR")),
            
            // 👇 Para todos
            new MenuOptionDTO("Cerrar sesión", "fa-sign-out-alt", "/logout", List.of("ADMINISTRADOR", "FARMACEUTICO", "GESTOR", "TRANSPORTISTA"))
        );
    }
}