package com.proyecto.auth.infrastructure.controller;

import com.proyecto.auth.application.dto.request.RegisterRequestDTO;
import com.proyecto.auth.application.dto.response.AdminEstadisticasDTO;
import com.proyecto.auth.application.dto.response.UsuarioResponseDTO;
import com.proyecto.auth.application.service.UsuarioService;
import com.proyecto.core.orden.domain.model.EstadoOrden;
import com.proyecto.core.orden.domain.repository.OrdenRepository;
import com.proyecto.core.sede.domain.repository.SedeRepository;
import com.proyecto.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;
    private final OrdenRepository ordenRepository;
    private final SedeRepository sedeRepository;

    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponse<AdminEstadisticasDTO>> estadisticas() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarUsuarios();
        long activos = usuarios.stream().filter(u -> Boolean.TRUE.equals(u.activo())).count();
        long pendientes = ordenRepository.findAll().stream()
            .filter(o -> o.getEstado() == EstadoOrden.PENDIENTE).count();
        long totalOrdenes = ordenRepository.count();
        long totalSedes = sedeRepository.count();
        return ResponseEntity.ok(ApiResponse.ok(
            new AdminEstadisticasDTO(activos, usuarios.size(), totalSedes, pendientes, totalOrdenes)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponseDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.listarUsuarios()));
    }

    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> obtener(@PathVariable String email) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(usuarioService.buscarPorEmail(email)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{email}")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> actualizar(
            @PathVariable String email, @RequestBody RegisterRequestDTO dto) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(usuarioService.actualizarUsuario(email, dto)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable String email) {
        try {
            usuarioService.eliminarUsuario(email);
            return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDTO>>> pendientes() {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.listarPendientes()));
    }

    @PatchMapping("/{email}/aprobar")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> aprobar(@PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.aprobarUsuario(email)));
    }
}
