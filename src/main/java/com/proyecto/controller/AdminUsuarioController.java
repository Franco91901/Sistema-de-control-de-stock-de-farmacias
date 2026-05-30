package com.proyecto.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.proyecto.dto.RegistroUsuarioDTO;
import com.proyecto.dto.UsuarioResponseDTO;
import com.proyecto.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;


    // ================= LISTAR =================
    @GetMapping
    public String listarUsuarios(Model model) {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin/lista";   // ✅ coincide con templates/admin/lista.html
    }

    // ================= VER DETALLE =================
    @GetMapping("/ver/{email}")
    public String verUsuario(@PathVariable String email, Model model) {
        UsuarioResponseDTO usuario = usuarioService.buscarPorEmail(email);
        model.addAttribute("usuario", usuario);
        return "admin/ver";     // ✅ coincide con templates/admin/ver.html
    }

    // ================= FORM EDITAR =================
    @GetMapping("/editar/{email}")
    public String mostrarFormularioEditar(@PathVariable String email, Model model) {

        UsuarioResponseDTO usuario = usuarioService.buscarPorEmail(email);

        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setTelefono(usuario.getTelefono());

        model.addAttribute("usuarioEmail", email);
        model.addAttribute("registroUsuarioDTO", dto);

        return "admin/editar";  // ✅ coincide con templates/admin/editar.html
    }

    // ================= PROCESAR EDICIÓN =================
    @PostMapping("/editar/{email}")
    public String actualizarUsuario(
            @PathVariable String email,
            @ModelAttribute RegistroUsuarioDTO dto) {

        usuarioService.actualizarUsuario(email, dto);
        return "redirect:/admin/usuarios";  // ✅ redirige a la lista
    }

    // ================= ELIMINAR =================
    @PostMapping("/eliminar/{email}")
    public String eliminarUsuario(@PathVariable String email) {
        usuarioService.eliminarUsuario(email);
        return "redirect:/admin/usuarios";  // ✅ redirige a la lista
    }
}
