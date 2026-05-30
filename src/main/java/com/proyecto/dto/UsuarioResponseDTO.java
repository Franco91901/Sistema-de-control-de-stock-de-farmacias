package com.proyecto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioResponseDTO {

    private Integer id;
    private String nombre;
    private String email;
    private String telefono;
    private String rol;
    private String sede;
}