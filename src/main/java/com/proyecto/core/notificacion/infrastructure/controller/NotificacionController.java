package com.proyecto.core.notificacion.infrastructure.controller;

import com.proyecto.core.notificacion.application.dto.NotificacionEstadisticasDTO;
import com.proyecto.core.notificacion.application.dto.NotificacionResponseDTO;
import com.proyecto.core.notificacion.application.service.NotificacionService;
import com.proyecto.core.notificacion.domain.model.TipoNotificacion;
import com.proyecto.shared.dto.ApiResponse;
import com.proyecto.shared.security.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final AuthContext authContext;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificacionResponseDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(notificacionService.listarNotificacionesPorSede(authContext.getIdSede())));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<ApiResponse<List<NotificacionResponseDTO>>> pendientes() {
        return ResponseEntity.ok(ApiResponse.ok(notificacionService.listarNotificacionesPendientes(authContext.getIdSede())));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<ApiResponse<List<NotificacionResponseDTO>>> porTipo(@PathVariable String tipo) {
        TipoNotificacion tipoEnum = TipoNotificacion.valueOf(tipo.toUpperCase());
        return ResponseEntity.ok(ApiResponse.ok(notificacionService.listarNotificacionesPorTipo(authContext.getIdSede(), tipoEnum)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificacionResponseDTO>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(notificacionService.obtenerNotificacionPorId(id)));
    }

    @PostMapping("/generar-automaticas")
    public ResponseEntity<ApiResponse<List<NotificacionResponseDTO>>> generarAutomaticas() {
        Long idSede = authContext.getIdSede();
        notificacionService.generarNotificacionesAutomaticas(idSede);
        return ResponseEntity.ok(ApiResponse.ok("Notificaciones generadas",
                notificacionService.listarNotificacionesPendientes(idSede)));
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponse<NotificacionEstadisticasDTO>> estadisticas() {
        Long idSede = authContext.getIdSede();
        return ResponseEntity.ok(ApiResponse.ok(new NotificacionEstadisticasDTO(
            notificacionService.contarNotificacionesPendientes(idSede),
            notificacionService.contarNotificacionesPorTipo(idSede, TipoNotificacion.BAJO_STOCK),
            notificacionService.contarNotificacionesPorTipo(idSede, TipoNotificacion.PROXIMO_CADUCAR)
        )));
    }
}
