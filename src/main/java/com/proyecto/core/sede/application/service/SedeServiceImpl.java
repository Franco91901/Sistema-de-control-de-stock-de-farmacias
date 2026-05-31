package com.proyecto.core.sede.application.service;

import com.proyecto.core.sede.application.dto.SedeDTO;
import com.proyecto.core.sede.application.mapper.SedeMapper;
import com.proyecto.core.sede.domain.model.Sede;
import com.proyecto.core.sede.domain.repository.SedeRepository;
import com.proyecto.core.sede.application.service.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SedeServiceImpl implements SedeService {

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private SedeMapper sedeMapper;

    @Override
    public List<SedeDTO> listarSedes() {
        return sedeRepository.findAll()
                .stream()
                .map(sedeMapper::toDTO)
                .collect(Collectors.toList());
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
