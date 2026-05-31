package com.proyecto.core.sede.application.service;

import com.proyecto.core.sede.application.dto.SedeDTO;
import com.proyecto.core.sede.application.mapper.SedeMapper;
import com.proyecto.core.sede.domain.model.Sede;
import com.proyecto.core.sede.domain.repository.SedeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SedeServiceImpl implements SedeService {

    private final SedeRepository sedeRepository;

    private final SedeMapper sedeMapper;

    @Override
    public List<SedeDTO> listarSedes() {
        return sedeRepository.findAll()
                .stream()
                .map(sedeMapper::toDTO).toList();
    }

    @Override
    public SedeDTO obtenerPorId(Long idSede) {
        return sedeRepository.findById(idSede)
                .map(sedeMapper::toDTO)
                .orElse(null);
    }

    @Override
    public SedeDTO crearSede(SedeDTO dto) {
        Sede sede = sedeMapper.toEntity(dto);
        return sedeMapper.toDTO(sedeRepository.save(sede));
    }

    @Override
    public SedeDTO actualizarSede(Long idSede, SedeDTO dto) {
        return sedeRepository.findById(idSede)
                .map(sede -> {
                    sede.setNombre(dto.nombre());
                    sede.setDireccion(dto.direccion());
                    return sedeMapper.toDTO(sedeRepository.save(sede));
                }).orElse(null);
    }

    @Override
    public void eliminarSede(Long idSede) {
        sedeRepository.deleteById(idSede);
    }
}
