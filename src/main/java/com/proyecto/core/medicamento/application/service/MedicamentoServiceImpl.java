package com.proyecto.core.medicamento.application.service;

import com.proyecto.core.medicamento.application.dto.MedicamentoRequestDTO;
import com.proyecto.core.medicamento.application.dto.MedicamentoResponseDTO;
import com.proyecto.core.stock.application.dto.StockMedicamentoResponseDTO;
import com.proyecto.core.medicamento.application.mapper.MedicamentoMapper;
import com.proyecto.core.stock.application.mapper.StockMapper;
import com.proyecto.core.medicamento.domain.model.Medicamento;
import com.proyecto.core.medicamento.domain.model.MedicamentoSede;
import com.proyecto.core.sede.domain.model.Sede;
import com.proyecto.core.medicamento.domain.repository.MedicamentoRepository;
import com.proyecto.core.medicamento.domain.repository.MedicamentoSedeRepository;
import com.proyecto.core.lote.domain.repository.LoteRepository;
import com.proyecto.core.sede.domain.repository.SedeRepository;
import com.proyecto.core.stock.application.service.FarmaceuticoConstants;
import com.proyecto.shared.exception.EntityNotFoundException;
import com.proyecto.shared.exception.ExceptionConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicamentoServiceImpl implements MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final MedicamentoSedeRepository medicamentoSedeRepository;
    private final SedeRepository sedeRepository;
    private final LoteRepository loteRepository;
    private final MedicamentoMapper medicamentoMapper;
    private final StockMapper stockMapper;

    @Override
    @Transactional
    public MedicamentoResponseDTO crearMedicamento(MedicamentoRequestDTO requestDTO, Long idSede) {
        if (existeMedicamentoEnSede(requestDTO.nombre(), idSede)) {
            throw new IllegalArgumentException("Ya existe un medicamento con ese nombre en esta sede");
        }

        Sede sede = sedeRepository.findById(idSede)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.SEDE_NO_ENCONTRADA));

        Medicamento medicamento = medicamentoMapper.toEntity(requestDTO);
        Medicamento saved = medicamentoRepository.save(medicamento);

        MedicamentoSede medSede = new MedicamentoSede(saved, sede);
        medicamentoSedeRepository.save(medSede);

        return medicamentoMapper.toResponseDTO(medSede);
    }

    @Override
    public MedicamentoResponseDTO obtenerMedicamentoPorId(Long idMedicamento) {
        Medicamento medicamento = medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.MEDICAMENTO_NO_ENCONTRADO));
        List<MedicamentoSede> sedes = medicamentoSedeRepository.findByMedicamentoIdMedicamento(idMedicamento);
        MedicamentoSede medSede = sedes.isEmpty() ? null : sedes.get(0);
        if (medSede == null) {
            return new MedicamentoResponseDTO(medicamento.getIdMedicamento(), medicamento.getNombre(),
                    medicamento.getDescripcion(), medicamento.getPrecioVenta(), 0, null, null, null, null, false);
        }
        return medicamentoMapper.toResponseDTO(medSede);
    }

    @Override
    public List<MedicamentoResponseDTO> listarMedicamentosPorSede(Long idSede) {
        return medicamentoSedeRepository.findBySedeIdSede(idSede).stream()
                .map(medicamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MedicamentoResponseDTO actualizarMedicamento(Long idMedicamento, MedicamentoRequestDTO requestDTO) {
        Medicamento medicamento = medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.MEDICAMENTO_NO_ENCONTRADO));

        if (!medicamento.getNombre().equalsIgnoreCase(requestDTO.nombre())) {
            Long idSede = requestDTO.idSede();
            if (idSede != null && existeMedicamentoEnSede(requestDTO.nombre(), idSede)) {
                throw new IllegalArgumentException("Ya existe un medicamento con ese nombre en esta sede");
            }
        }

        medicamentoMapper.updateEntity(medicamento, requestDTO);
        medicamentoRepository.save(medicamento);

        List<MedicamentoSede> sedes = medicamentoSedeRepository.findByMedicamentoIdMedicamento(idMedicamento);
        MedicamentoSede medSede = sedes.isEmpty() ? null : sedes.get(0);
        if (medSede == null) {
            return new MedicamentoResponseDTO(medicamento.getIdMedicamento(), medicamento.getNombre(),
                    medicamento.getDescripcion(), medicamento.getPrecioVenta(), 0, null, null, null, null, false);
        }
        return medicamentoMapper.toResponseDTO(medSede);
    }

    @Override
    @Transactional
    public void eliminarMedicamento(Long idMedicamento) {
        List<com.proyecto.core.lote.domain.model.Lote> lotes = loteRepository.findByMedicamentoIdMedicamento(idMedicamento);
        if (!lotes.isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el medicamento porque tiene lotes asociados");
        }
        medicamentoSedeRepository.findByMedicamentoIdMedicamento(idMedicamento)
                .forEach(medicamentoSedeRepository::delete);
        medicamentoRepository.deleteById(idMedicamento);
    }

    @Override
    public boolean existeMedicamentoEnSede(String nombre, Long idSede) {
        return medicamentoSedeRepository.findByNombreIgnoreCaseAndSede(nombre, idSede).isPresent();
    }

    @Override
    public boolean validarStockSuficiente(Long idMedicamento, Integer cantidad) {
        Integer stockTotal = loteRepository.sumStockByMedicamento(idMedicamento);
        return stockTotal != null && stockTotal >= cantidad;
    }

    @Override
    public StockMedicamentoResponseDTO obtenerStockMedicamento(Long idMedicamento) {
        Medicamento medicamento = medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.MEDICAMENTO_NO_ENCONTRADO));
        List<MedicamentoSede> sedes = medicamentoSedeRepository.findByMedicamentoIdMedicamento(idMedicamento);
        MedicamentoSede medSede = sedes.isEmpty() ? null : sedes.get(0);
        if (medSede == null) {
            return new StockMedicamentoResponseDTO(medicamento.getIdMedicamento(), medicamento.getNombre(),
                    medicamento.getDescripcion(), 0, null, null, 0, 0, "CRITICO", "danger");
        }
        List<com.proyecto.core.lote.domain.model.Lote> lotes =
                loteRepository.findByMedicamentoIdMedicamento(idMedicamento);
        return stockMapper.toStockResponseDTO(medSede, lotes);
    }

    @Override
    public List<StockMedicamentoResponseDTO> listarStockPorSede(Long idSede) {
        return medicamentoSedeRepository.findBySedeIdSede(idSede).stream()
                .map(ms -> {
                    List<com.proyecto.core.lote.domain.model.Lote> lotes =
                            loteRepository.findByMedicamentoIdMedicamento(ms.getMedicamento().getIdMedicamento());
                    return stockMapper.toStockResponseDTO(ms, lotes);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicamentoResponseDTO> buscarMedicamentosPorNombre(String nombre, Long idSede) {
        return medicamentoSedeRepository.findBySedeIdSede(idSede).stream()
                .filter(ms -> ms.getMedicamento().getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .map(medicamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicamentoResponseDTO> buscarMedicamentosBajoStock(Long idSede) {
        return medicamentoSedeRepository.findMedicamentosBajoStock(idSede, FarmaceuticoConstants.UMBRAL_BAJO_STOCK).stream()
                .map(medicamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
