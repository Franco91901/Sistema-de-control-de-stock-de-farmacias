package com.proyecto.service;

import com.proyecto.dto.NotificacionResponseDTO;
import com.proyecto.dto.StockPorMedicamentoDto;
import com.proyecto.dto.StockPorSedeDto;
import com.proyecto.model.Notificacion;
import com.proyecto.model.Orden;
import java.util.List;

public interface GestorService {
	
	List<com.proyecto.model.Medicamento> listarMedicamentosBajoStock(Long idSede);

    // Stock
    Integer obtenerStockPorMedicamento(Long idMedicamento);
    Integer obtenerStockPorSede(Long idSede);
    List<com.proyecto.model.Medicamento> listarMedicamentos();
    List<com.proyecto.model.Sede> listarSedes();

    // Notificaciones
    List<NotificacionResponseDTO> obtenerNotificacionesConNombres();
    List<com.proyecto.dto.NotificacionDto> obtenerNotificacionesConNombresParaPdf();
    List<Notificacion> listarNotificaciones();

    // Órdenes
    Orden generarOrdenDesdeNotificacion(Long idGestor, Long idNotificacion, Integer cantidadSolicitada);
    List<Orden> listarOrdenesPorGestor(Long idGestor);
    List<com.proyecto.model.DetalleOrden> obtenerDetallesDeOrden(Long idOrden);
    void eliminarOrden(Long idOrden);

    // Reportes
    List<StockPorSedeDto> obtenerStockPorSedeParaReporte();
    List<StockPorMedicamentoDto> obtenerStockPorMedicamentoParaReporte();
    List<com.proyecto.model.Notificacion> obtenerNotificacionesParaReporte();
    List<Orden> obtenerOrdenesParaReporte(Long idGestor);

    // Otros
    String obtenerNombreUsuario(Long idUsuario);
    String obtenerNombreSede(Long idSede);
    String obtenerNombreMedicamento(Long idMedicamento);
    Notificacion obtenerNotificacionPorId(Long idNotificacion);  
}