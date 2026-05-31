package com.proyecto.auth.infrastructure.controller;

import com.proyecto.auth.application.dto.request.RegisterRequestDTO;
import com.proyecto.auth.application.dto.response.UsuarioResponseDTO;
import com.proyecto.auth.application.service.UsuarioService;
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
}
