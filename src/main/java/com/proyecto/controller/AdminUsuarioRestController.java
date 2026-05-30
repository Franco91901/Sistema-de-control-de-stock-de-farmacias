package com.proyecto.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.proyecto.dto.RegistroUsuarioDTO;
import com.proyecto.dto.UsuarioResponseDTO;
import com.proyecto.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
public class AdminUsuarioRestController {

    private final UsuarioService usuarioService;

    // ================= LISTAR =================
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // ================= VER DETALLE =================
    @GetMapping("/{email}")
    public ResponseEntity<UsuarioResponseDTO> verUsuario(@PathVariable String email) {
        UsuarioResponseDTO usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    // ================= EDITAR =================
    @PutMapping("/{email}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable String email,
            @RequestBody RegistroUsuarioDTO dto) {

        UsuarioResponseDTO usuarioActualizado = usuarioService.actualizarUsuario(email, dto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // ================= ELIMINAR =================
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String email) {
        usuarioService.eliminarUsuario(email);
        return ResponseEntity.noContent().build();
    }
}
