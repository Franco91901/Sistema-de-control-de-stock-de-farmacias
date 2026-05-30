package com.proyecto.service;

import com.proyecto.dto.SedeDTO;
import com.proyecto.mapper.SedeMapper;
import com.proyecto.model.Sede;
import com.proyecto.repository.SedeRepository;
import com.proyecto.service.SedeService;
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
                    sede.setNombre(dto.getNombre());
                    sede.setDireccion(dto.getDireccion());
                    return sedeMapper.toDTO(sedeRepository.save(sede));
                }).orElse(null);
    }

    @Override
    public void eliminarSede(Long idSede) {
        sedeRepository.deleteById(idSede);
    }
}
