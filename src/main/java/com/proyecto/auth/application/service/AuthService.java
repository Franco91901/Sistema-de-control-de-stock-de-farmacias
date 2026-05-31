package com.proyecto.auth.application.service;

import com.proyecto.auth.application.dto.request.LoginRequestDTO;
import com.proyecto.auth.application.dto.request.RegisterRequestDTO;

import java.util.Map;

public interface AuthService {
    Map<String, String> login(LoginRequestDTO dto);
    void register(RegisterRequestDTO dto);
    boolean validateToken(String token);
    String getEmailFromToken(String token);
}
