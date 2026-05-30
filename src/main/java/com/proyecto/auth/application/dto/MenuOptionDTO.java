package com.proyecto.auth.application.dto;

import java.util.List;

public record MenuOptionDTO(
    String label,
    String icon,
    String url,
    List<String> allowedRoles
) {}
