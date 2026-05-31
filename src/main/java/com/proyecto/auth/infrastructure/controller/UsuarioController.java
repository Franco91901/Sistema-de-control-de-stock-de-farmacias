package com.proyecto.auth.infrastructure.controller;

import com.proyecto.auth.application.dto.response.UsuarioResponseDTO;
import com.proyecto.auth.application.service.UsuarioService;
import com.proyecto.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponseDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.listarUsuarios()));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> obtenerPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.obtenerPorEmail(email)));
    }
}
