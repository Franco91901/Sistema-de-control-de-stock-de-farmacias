package com.proyecto.auth.infrastructure.controller;

import com.proyecto.auth.application.dto.response.AuthResponseDTO;
import com.proyecto.auth.application.dto.request.LoginRequestDTO;
import com.proyecto.auth.application.dto.request.RegisterRequestDTO;
import com.proyecto.auth.application.dto.response.UsuarioResponseDTO;
import com.proyecto.auth.application.mapper.UsuarioMapper;
import com.proyecto.auth.application.service.AuthService;
import com.proyecto.auth.domain.model.Usuario;
import com.proyecto.auth.domain.repository.UsuarioRepository;
import com.proyecto.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO dto) {
        Map<String, String> tokenMap = authService.login(dto);
        String token = tokenMap.get("access-token");
        Usuario usuario = usuarioRepository.findByEmail(dto.email()).orElseThrow();
        UsuarioResponseDTO usuarioDTO = UsuarioMapper.toResponseDTO(usuario);
        return ResponseEntity.ok(ApiResponse.ok(new AuthResponseDTO(usuarioDTO, token)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO dto) {
        authService.register(dto);
        Map<String, String> tokenMap = authService.login(new LoginRequestDTO(dto.email(), dto.password()));
        String token = tokenMap.get("access-token");
        Usuario usuario = usuarioRepository.findByEmail(dto.email()).orElseThrow();
        UsuarioResponseDTO usuarioDTO = UsuarioMapper.toResponseDTO(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Usuario registrado", new AuthResponseDTO(usuarioDTO, token)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> me( @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(ApiResponse.ok(UsuarioMapper.toResponseDTO(usuario)));
    }
}
