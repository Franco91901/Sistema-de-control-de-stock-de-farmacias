package com.proyecto.core.orden.infrastructure.controller;

import com.proyecto.core.notificacion.application.dto.NotificacionResponseDTO;
import com.proyecto.core.notificacion.application.dto.NotificacionVistaDTO;
import com.proyecto.core.orden.domain.model.DetalleOrden;
import com.proyecto.core.orden.domain.model.Orden;
import com.proyecto.core.stock.application.dto.StockPorMedicamentoDto;
import com.proyecto.core.stock.application.dto.StockPorSedeDto;
import com.proyecto.core.medicamento.domain.model.Medicamento;
import com.proyecto.core.notificacion.domain.model.Notificacion;
import com.proyecto.core.orden.application.service.GestorService;
import com.proyecto.core.notificacion.application.service.NotificacionService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@RequestMapping("/gestor")
@RequiredArgsConstructor
public class GestorController {


    private final GestorService gestorService;

    private final NotificacionService notificacionService;

    
    @GetMapping("/stock")
    public String verStockGeneral(Model model) {
        model.addAttribute("sedes", gestorService.listarSedes());

        List<Medicamento> medicamentos = gestorService.listarMedicamentos();
        for (Medicamento m : medicamentos) {
        	m.setNombreSede(gestorService.obtenerNombreSede(m.getSede().getIdSede()));
            m.setStockTotal(gestorService.obtenerStockPorMedicamento(m.getIdMedicamento()));
        }
        model.addAttribute("medicamentos", medicamentos);

        return "stock-general";
    }

    @GetMapping("/stock/medicamento/{id}")
    @ResponseBody
    public Integer stockPorMedicamento(@PathVariable Long id) {
        return gestorService.obtenerStockPorMedicamento(id);
    }

    @GetMapping("/stock/sede/{id}")
    @ResponseBody
    public Integer stockPorSede(@PathVariable Long id) {
        return gestorService.obtenerStockPorSede(id);
    }

    
    @GetMapping("/notificaciones")
    public String listarNotificaciones(Model model) {
        Long idSede = 1L; // temporal
        List<NotificacionResponseDTO> notificaciones = notificacionService.listarNotificacionesPorSede(idSede);
        
        List<NotificacionVistaDTO> vistaNotificaciones = notificaciones.stream()
            .map(n -> new NotificacionVistaDTO(
                n.idNotificacion(), n.mensaje(), n.fechaFormateada(),
                n.estado(), n.nombreMedicamento(), n.nombreSede()))
            .collect(Collectors.toList());
        
        model.addAttribute("notificaciones", vistaNotificaciones);
        return "notificaciones";
    }

    
    @GetMapping("/notificacion/{id}")
    @ResponseBody
    public Notificacion detalleNotificacion(@PathVariable Long id) {
        return gestorService.obtenerNotificacionPorId(id);
    }

    

    @PostMapping("/ordenes/generar")
    public String generarOrden(
            @RequestParam Long idNotificacion,
            @RequestParam Integer cantidad,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        Long idGestor = (Long) session.getAttribute("idUsuario");
        if (idGestor == null) {
            idGestor = 3L; // modo prueba
            System.out.println("⚠️ Modo prueba: Usando ID Gestor = 3");
        }

        try {
            gestorService.generarOrdenDesdeNotificacion(idGestor, idNotificacion, cantidad);
            redirectAttrs.addFlashAttribute("mensaje", "¡Orden generada exitosamente!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al generar la orden: " + e.getMessage());
        }

        return "redirect:/gestor/notificaciones";
    }

    // ==============================
    // 3.4: ÓRDENES GENERADAS
    // ==============================

    @GetMapping("/ordenes")
    public String listarOrdenes(HttpSession session, Model model) {
        Long idGestor = (Long) session.getAttribute("idUsuario");
        if (idGestor == null) idGestor = 3L;
        model.addAttribute("ordenes", gestorService.listarOrdenesPorGestor(idGestor));
        return "ordenes";
    }
    
    @GetMapping("/order/{id}")
    public String verDetalleOrden(@PathVariable Long id, HttpSession session, Model model) {
        Long idGestor = (Long) session.getAttribute("idUsuario");
        if (idGestor == null) idGestor = 3L;

        Orden orden = gestorService.listarOrdenesPorGestor(idGestor).stream()
                .filter(o -> o.getIdOrden().equals(id)) // 👈 Ya es Long, sin .intValue()
                .findFirst()
                .orElse(null);

        if (orden == null) {
            model.addAttribute("error", "Orden no encontrada");
            return "redirect:/gestor/ordenes";
        }

        model.addAttribute("orden", orden);
        model.addAttribute("nombreGestor", gestorService.obtenerNombreUsuario(orden.getIdGestor()));

        List<Map<String, Object>> detalles = new ArrayList<>();
        for (DetalleOrden d : gestorService.obtenerDetallesDeOrden(id)) { // 👈 Ya es Long
            detalles.add(Map.of(
                "nombreMedicamento", gestorService.obtenerNombreMedicamento(d.getMedicamento().getIdMedicamento()), // ✅ Corregido
                "cantidad", d.getCantidad(),
                "estado", d.getEstado()
            ));
        }
        model.addAttribute("detalles", detalles);

        return "detalle-orden";
    }
    
    @PostMapping("/ordenes/eliminar/{id}")
    public String eliminarOrden(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            gestorService.eliminarOrden(id);
            redirectAttrs.addFlashAttribute("mensaje", "Orden eliminada correctamente");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo eliminar la orden");
        }
        return "redirect:/gestor/ordenes";
    }

    // ==============================
    // REPORTES PDF
    // ==============================

    @GetMapping("/reportes/stock-sede/pdf")
    public void generarReporteStockPorSede(HttpServletResponse response) throws Exception {
        List<StockPorSedeDto> data = gestorService.obtenerStockPorSedeParaReporte();
        generatePdf("stock_por_sede.jrxml", "Stock Por Sede", data, response);
    }

    @GetMapping("/reportes/stock-medicamento/pdf")
    public void generarReporteStockPorMedicamento(HttpServletResponse response) throws Exception {
        List<StockPorMedicamentoDto> data = gestorService.obtenerStockPorMedicamentoParaReporte();
        generatePdf("stock_por_medicamento.jrxml", "Stock Por Medicamento", data, response);
    }

    @GetMapping("/reportes/notificaciones/pdf")
    public void generarReporteNotificaciones(HttpServletResponse response) throws Exception {
        List<com.proyecto.core.notificacion.application.dto.NotificacionDto> data = gestorService.obtenerNotificacionesConNombresParaPdf();
        generatePdf("notificaciones.jrxml", "Notificaciones", data, response);
    }

    @GetMapping("/reportes/ordenes/pdf")
    public void generarReporteOrdenes(HttpServletResponse response) throws Exception {
        Long idGestor = 3L;
        List<Orden> data = gestorService.obtenerOrdenesParaReporte(idGestor);
        generatePdf("ordenes.jrxml", "Órdenes", data, response);
    }

    private void generatePdf(String jrxmlFile, String title, List<?> data, HttpServletResponse response) throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("reports/" + jrxmlFile);
        if (inputStream == null) {
            throw new FileNotFoundException("Plantilla no encontrada: " + jrxmlFile);
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("Title", title);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + title.replace(" ", "_") + ".pdf");

        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }
}