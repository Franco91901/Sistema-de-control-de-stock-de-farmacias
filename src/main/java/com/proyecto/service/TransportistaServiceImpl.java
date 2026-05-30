package com.proyecto.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.dto.OrdenTransportistaDTO;
import com.proyecto.mapper.OrdenTransportistaMapper;
import com.proyecto.model.DetalleOrden;
import com.proyecto.repository.DetalleOrdenRepository;

@Service
public class TransportistaServiceImpl implements TransportistaService {

    private final DetalleOrdenRepository repo;

    public TransportistaServiceImpl(DetalleOrdenRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<OrdenTransportistaDTO> listarOrdenes(String estado, String sede) {
        return repo.listarPorEstadoYSede(estado, sede)
                   .stream()
                   .map(OrdenTransportistaMapper::toDTO)
                   .collect(Collectors.toList());
    }

    @Override
    public OrdenTransportistaDTO obtenerDetalle(Long idDetalle) {
        DetalleOrden d = repo.findById(idDetalle).orElse(null);
        return d != null ? OrdenTransportistaMapper.toDTO(d) : null;
    }

    @Transactional
    @Override
    public void avanzarEstado(Long idDetalle) {
        DetalleOrden d = repo.findById(idDetalle).orElseThrow();

        switch (d.getEstado()) {
            case "PENDIENTE":
                d.setEstado("EN PREPARACION");
                break;
            case "EN PREPARACION":
                d.setEstado("EN RUTA");
                break;
            case "EN RUTA":
                d.setEstado("ENTREGADO");
                break;
        }

        repo.save(d);
    }
}
