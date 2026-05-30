package com.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroUsuarioDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String email;

    @NotBlank
    private String telefono;
    
    @NotBlank
    private String password;

    private Integer rolId;
    private Long sedeId;
}
