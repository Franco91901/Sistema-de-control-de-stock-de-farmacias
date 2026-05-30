package com.proyecto.controller;

import com.proyecto.dto.SedeDTO;
import com.proyecto.service.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/sedes")
public class SedeController {

    @Autowired
    private SedeService sedeService;

    /**
     * Lista todas las sedes
     */
    @GetMapping
    public String listarSedes(Model model) {
        model.addAttribute("sedes", sedeService.listarSedes());
        return "sedes/lista"; // plantilla Thymeleaf en templates/sede/lista.html
    }

    /**
     * Ver detalle de una sede por ID
     */
    @GetMapping("/{id}")
    public String verSede(@PathVariable Long id, Model model) {
        SedeDTO sede = sedeService.obtenerPorId(id);
        model.addAttribute("sede", sede);
        return "sedes/ver"; // plantilla Thymeleaf en templates/sede/ver.html
    }
}
