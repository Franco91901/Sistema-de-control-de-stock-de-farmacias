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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO dto) {
        Map<String, String> tokenMap = authService.login(dto);
        String token = tokenMap.get("access-token");
        Usuario usuario = usuarioRepository.findByEmail(dto.email()).orElseThrow();
        UsuarioResponseDTO usuarioDTO = UsuarioMapper.toResponseDTO(usuario);
        return ResponseEntity.ok(ApiResponse.ok(new AuthResponseDTO(usuarioDTO, token)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequestDTO dto) {
        authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registro exitoso. Tu cuenta está pendiente de aprobación por el administrador.", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> me(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(ApiResponse.ok(UsuarioMapper.toResponseDTO(usuario)));
    }

    /** TEMPORAL — eliminar después de confirmar que el admin puede loguearse */
    @GetMapping("/dev/fix-admin")
    public ResponseEntity<String> fixAdmin() {
        return usuarioRepository.findByEmail("admin@farmacia.pe").map(admin -> {
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setActivo(true);
            usuarioRepository.save(admin);
            return ResponseEntity.ok("Admin reseteado. Usa: admin@farmacia.pe / password");
        }).orElse(ResponseEntity.ok("Usuario admin@farmacia.pe no encontrado en la BD"));
    }
}
