package com.proyecto.service;

import com.proyecto.dto.SedeDTO;

import java.util.List;

public interface SedeService {

    List<SedeDTO> listarSedes();
    SedeDTO obtenerPorId(Long idSede);
    SedeDTO crearSede(SedeDTO dto);
    SedeDTO actualizarSede(Long idSede, SedeDTO dto);
    void eliminarSede(Long idSede);
}
