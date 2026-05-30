package com.proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.proyecto.repository.SedeRepository;
import com.proyecto.service.TransportistaService;

@Controller
@RequestMapping("/transportista/ordenes")
public class TransportistaController {

    private final TransportistaService service;
    private final SedeRepository sedeRepo;

    public TransportistaController(
            TransportistaService service,
            SedeRepository sedeRepo) {
        this.service = service;
        this.sedeRepo = sedeRepo;
    }

    @GetMapping
    public String vistaTransportista(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String sede,
            @RequestParam(required = false) Long idDetalle,
            Model model) {

        if (estado != null && estado.isEmpty()) estado = null;
        if (sede != null && sede.isEmpty()) sede = null;

        model.addAttribute("ordenes", service.listarOrdenes(estado, sede));
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("sedeSeleccionada", sede);

        // 🔽 lista de sedes para el combo
        model.addAttribute("sedes", sedeRepo.findAll());

        if (idDetalle != null) {
            model.addAttribute("ordenSeleccionada", service.obtenerDetalle(idDetalle));
        }

        return "transportista/ordenes";
    }

    @PostMapping("/avanzar/{id}")
    public String avanzarEstado(
            @PathVariable("id") Long idDetalle,
            @RequestParam(required = false) String estado) {

        service.avanzarEstado(idDetalle);
        return "redirect:/transportista/ordenes?estado=" + (estado != null ? estado : "");
    }
}
