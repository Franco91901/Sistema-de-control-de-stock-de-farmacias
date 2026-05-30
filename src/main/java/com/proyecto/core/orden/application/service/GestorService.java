package com.proyecto.core.orden.application.service;

import com.proyecto.core.notificacion.application.dto.NotificacionResponseDTO;
import com.proyecto.core.stock.application.dto.StockPorMedicamentoDto;
import com.proyecto.core.stock.application.dto.StockPorSedeDto;
import com.proyecto.core.notificacion.domain.model.Notificacion;
import com.proyecto.core.orden.domain.model.Orden;
import java.util.List;

public interface GestorService {
	
	List<com.proyecto.core.medicamento.domain.model.Medicamento> listarMedicamentosBajoStock(Long idSede);

    // Stock
    Integer obtenerStockPorMedicamento(Long idMedicamento);
    Integer obtenerStockPorSede(Long idSede);
    List<com.proyecto.core.medicamento.domain.model.Medicamento> listarMedicamentos();
    List<com.proyecto.core.sede.domain.model.Sede> listarSedes();

    // Notificaciones
    List<NotificacionResponseDTO> obtenerNotificacionesConNombres();
    List<com.proyecto.core.notificacion.application.dto.NotificacionDto> obtenerNotificacionesConNombresParaPdf();
    List<Notificacion> listarNotificaciones();

    // Órdenes
    Orden generarOrdenDesdeNotificacion(Long idGestor, Long idNotificacion, Integer cantidadSolicitada);
    List<Orden> listarOrdenesPorGestor(Long idGestor);
    List<com.proyecto.core.orden.domain.model.DetalleOrden> obtenerDetallesDeOrden(Long idOrden);
    void eliminarOrden(Long idOrden);

    // Reportes
    List<StockPorSedeDto> obtenerStockPorSedeParaReporte();
    List<StockPorMedicamentoDto> obtenerStockPorMedicamentoParaReporte();
    List<com.proyecto.core.notificacion.domain.model.Notificacion> obtenerNotificacionesParaReporte();
    List<Orden> obtenerOrdenesParaReporte(Long idGestor);

    // Otros
    String obtenerNombreUsuario(Long idUsuario);
    String obtenerNombreSede(Long idSede);
    String obtenerNombreMedicamento(Long idMedicamento);
    Notificacion obtenerNotificacionPorId(Long idNotificacion);  
}