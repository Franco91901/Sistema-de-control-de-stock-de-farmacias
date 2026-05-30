package com.proyecto.core.sede.application.service;

import com.proyecto.core.sede.application.dto.SedeDTO;

import java.util.List;

public interface SedeService {

    List<SedeDTO> listarSedes();
    SedeDTO obtenerPorId(Long idSede);
    SedeDTO crearSede(SedeDTO dto);
    SedeDTO actualizarSede(Long idSede, SedeDTO dto);
    void eliminarSede(Long idSede);
}
