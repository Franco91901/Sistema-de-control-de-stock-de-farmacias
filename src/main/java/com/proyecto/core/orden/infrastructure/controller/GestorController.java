package com.proyecto.core.orden.infrastructure.controller;

import com.proyecto.core.notificacion.application.dto.NotificacionResponseDTO;
import com.proyecto.core.notificacion.application.service.NotificacionService;
import com.proyecto.core.orden.application.dto.DetalleItemDTO;
import com.proyecto.core.orden.application.dto.OrdenDetalleDTO;
import com.proyecto.core.orden.application.dto.OrdenResponseDTO;
import com.proyecto.core.orden.application.mapper.OrdenMapper;
import com.proyecto.core.orden.application.service.GestorService;
import com.proyecto.core.orden.domain.model.Orden;
import com.proyecto.core.stock.application.dto.StockPorMedicamentoDto;
import com.proyecto.shared.dto.ApiResponse;
import com.proyecto.shared.exception.EntityNotFoundException;
import com.proyecto.shared.exception.ExceptionConstants;
import com.proyecto.shared.security.AuthContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gestor")
@RequiredArgsConstructor
public class GestorController {

    private final GestorService gestorService;
    private final NotificacionService notificacionService;
    private final AuthContext authContext;
    private final OrdenMapper ordenMapper;

    @GetMapping("/stock")
    public ResponseEntity<ApiResponse<List<StockPorMedicamentoDto>>> stockGeneral() {
        return ResponseEntity.ok(ApiResponse.ok(gestorService.obtenerStockPorMedicamentoParaReporte()));
    }

    @GetMapping("/stock/medicamento/{id}")
    public ResponseEntity<ApiResponse<Integer>> stockPorMedicamento(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(gestorService.obtenerStockPorMedicamento(id)));
    }

    @GetMapping("/stock/sede/{id}")
    public ResponseEntity<ApiResponse<Integer>> stockPorSede(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(gestorService.obtenerStockPorSede(id)));
    }

    @GetMapping("/notificaciones")
    public ResponseEntity<ApiResponse<List<NotificacionResponseDTO>>> notificaciones() {
        return ResponseEntity.ok(ApiResponse.ok(gestorService.obtenerNotificacionesConNombres()));
    }

    @GetMapping("/notificacion/{id}")
    public ResponseEntity<ApiResponse<NotificacionResponseDTO>> detalleNotificacion(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(notificacionService.obtenerNotificacionPorId(id)));
    }

    @PostMapping("/ordenes/generar")
    public ResponseEntity<ApiResponse<Long>> generarOrden(
            @RequestParam Long idNotificacion, @RequestParam Integer cantidad) {
        Orden orden = gestorService.generarOrdenDesdeNotificacion(
                authContext.getIdUsuario(), idNotificacion, cantidad);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Orden generada", orden.getIdOrden()));
    }

    @GetMapping("/ordenes")
    public ResponseEntity<ApiResponse<List<OrdenResponseDTO>>> listarOrdenes() {
        List<OrdenResponseDTO> ordenes = gestorService.listarOrdenesPorGestor(authContext.getIdUsuario())
                .stream().map(ordenMapper::toResponseDTO).toList();
        return ResponseEntity.ok(ApiResponse.ok(ordenes));
    }

    @GetMapping("/ordenes/{id}")
    public ResponseEntity<ApiResponse<OrdenDetalleDTO>> detalleOrden(@PathVariable Long id) {
        Orden orden = gestorService.listarOrdenesPorGestor(authContext.getIdUsuario()).stream()
                .filter(o -> o.getIdOrden().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.ORDEN_NO_ENCONTRADA));

        List<DetalleItemDTO> items = gestorService.obtenerDetallesDeOrden(id).stream()
                .map(d -> new DetalleItemDTO(
                    gestorService.obtenerNombreMedicamento(d.getMedicamento().getIdMedicamento()),
                    d.getCantidad(),
                    d.getEstado() != null ? d.getEstado().name() : null))
                .toList();

        OrdenDetalleDTO dto = new OrdenDetalleDTO(
            orden.getIdOrden(),
            orden.getUsuario() != null ? orden.getUsuario().getNombre() : "",
            orden.getTipo() != null ? orden.getTipo().name() : null,
            orden.getEstado() != null ? orden.getEstado().name() : null,
            orden.getFecha(),
            orden.getSede() != null ? orden.getSede().getNombre() : "",
            items
        );
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @DeleteMapping("/ordenes/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarOrden(@PathVariable Long id) {
        gestorService.eliminarOrden(id);
        return ResponseEntity.ok(ApiResponse.ok("Orden eliminada", null));
    }

    // ========== REPORTES PDF ==========

    @GetMapping("/reportes/stock-sede/pdf")
    public void reporteStockSede(HttpServletResponse response) throws Exception {
        generatePdf("stock_por_sede.jrxml", "Stock Por Sede", gestorService.obtenerStockPorSedeParaReporte(), response);
    }

    @GetMapping("/reportes/stock-medicamento/pdf")
    public void reporteStockMedicamento(HttpServletResponse response) throws Exception {
        generatePdf("stock_por_medicamento.jrxml", "Stock Por Medicamento", gestorService.obtenerStockPorMedicamentoParaReporte(), response);
    }

    @GetMapping("/reportes/notificaciones/pdf")
    public void reporteNotificaciones(HttpServletResponse response) throws Exception {
        generatePdf("notificaciones.jrxml", "Notificaciones", gestorService.obtenerNotificacionesConNombresParaPdf(), response);
    }

    @GetMapping("/reportes/ordenes/pdf")
    public void reporteOrdenes(HttpServletResponse response) throws Exception {
        generatePdf("ordenes.jrxml", "Órdenes", gestorService.obtenerOrdenesParaReporte(authContext.getIdUsuario()), response);
    }

    private void generatePdf(String jrxmlFile, String title, List<?> data, HttpServletResponse response) throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("reports/" + jrxmlFile);
        if (inputStream == null) throw new FileNotFoundException("Plantilla no encontrada: " + jrxmlFile);
        JasperReport report = JasperCompileManager.compileReport(inputStream);
        Map<String, Object> params = new HashMap<>();
        params.put("Title", title);
        JasperPrint print = JasperFillManager.fillReport(report, params, new JRBeanCollectionDataSource(data));
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + title.replace(" ", "_") + ".pdf");
        JasperExportManager.exportReportToPdfStream(print, response.getOutputStream());
    }
}
