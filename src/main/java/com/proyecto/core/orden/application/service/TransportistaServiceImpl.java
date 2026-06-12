package com.proyecto.core.orden.application.service;

import com.proyecto.core.orden.application.dto.OrdenTransportistaDTO;
import com.proyecto.core.orden.application.mapper.OrdenTransportistaMapper;
import com.proyecto.core.orden.domain.model.DetalleOrden;
import com.proyecto.core.orden.domain.model.EstadoDetalle;
import com.proyecto.core.orden.domain.model.EstadoOrden;
import com.proyecto.core.orden.domain.repository.DetalleOrdenRepository;
import com.proyecto.core.orden.domain.repository.OrdenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransportistaServiceImpl implements TransportistaService {

    private final DetalleOrdenRepository repo;
    private final OrdenRepository ordenRepository;

    public TransportistaServiceImpl(DetalleOrdenRepository repo, OrdenRepository ordenRepository) {
        this.repo = repo;
        this.ordenRepository = ordenRepository;
    }

    @Override
    public List<OrdenTransportistaDTO> listarOrdenes(String estado, String sede) {
        return repo.listarPorEstadoYSede(estado, sede).stream()
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
            case PENDIENTE      -> d.setEstado(EstadoDetalle.EN_PREPARACION);
            case EN_PREPARACION -> d.setEstado(EstadoDetalle.EN_RUTA);
            case EN_RUTA        -> d.setEstado(EstadoDetalle.ENTREGADO);
            default             -> {}
        }
        repo.save(d);

        if (d.getEstado() == EstadoDetalle.ENTREGADO) {
            var orden = d.getOrden();
            boolean todosEntregados = repo.findByOrdenIdOrden(orden.getIdOrden())
                .stream().allMatch(det -> det.getEstado() == EstadoDetalle.ENTREGADO);
            if (todosEntregados) {
                orden.setEstado(EstadoOrden.COMPLETADA);
                ordenRepository.save(orden);
            }
        }
    }
}
