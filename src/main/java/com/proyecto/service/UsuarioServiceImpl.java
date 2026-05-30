package com.proyecto.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.proyecto.dto.LoginResponseDTO;
import com.proyecto.dto.RegistroUsuarioDTO;
import com.proyecto.dto.UsuarioResponseDTO;
import com.proyecto.mapper.UsuarioMapperManual;
import com.proyecto.model.Usuario;
import com.proyecto.model.Rol;
import com.proyecto.model.Sede;
import com.proyecto.repository.UsuarioRepository;
import com.proyecto.security.JwtService;
import com.proyecto.repository.RolRepository;
import com.proyecto.repository.SedeRepository;
import com.proyecto.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final SedeRepository sedeRepository;
    private final PasswordEncoder passwordEncoder; // Para encriptar passwords

    // --------------------------
    // Crear usuario (registro)
    // --------------------------
    @Override
    public UsuarioResponseDTO crearUsuario(RegistroUsuarioDTO dto) {

        Rol rol;
        if (dto.getRolId() != null) {
            rol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        } else {
        	rol = rolRepository.findByNombre("USER")
        		       .orElseThrow(() -> new RuntimeException("Rol por defecto no encontrado"));
        }

        Sede sede = null;
        if (dto.getSedeId() != null) {
            sede = sedeRepository.findById(dto.getSedeId())
                    .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
        }

        Usuario usuario = UsuarioMapperManual.fromRegistroDTO(dto, rol, sede);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        usuarioRepository.save(usuario);

        return UsuarioMapperManual.toResponseDTO(usuario);
    }

    // --------------------------
    // Listar usuarios
    // --------------------------
    @Override
    public List<UsuarioResponseDTO> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(UsuarioMapperManual::toResponseDTO)
                .collect(Collectors.toList());
    }

    // --------------------------
    // Obtener usuario por email
    // --------------------------
    @Override
    public UsuarioResponseDTO obtenerPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return UsuarioMapperManual.toResponseDTO(usuario);
    }

    // --------------------------
    // Login
    // --------------------------
    @Override
    public LoginResponseDTO login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Password incorrecta");
        }

        // Usamos el mapper actualizado que ya no tiene token
        return UsuarioMapperManual.toLoginResponseDTO(usuario);
    }


    @Override
    public void enviarTokenRecuperacion(String email) {
        // Simplemente validamos que el usuario exista, ya no se genera token
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Aquí podrías enviar directamente un mensaje con instrucciones
        // sin token
    }

    @Override
    public void resetPassword(String token, String nuevaPassword) {
        throw new UnsupportedOperationException("Reset de password con token deshabilitado.");
    }
    
 // --------------------------
 // Buscar usuario (ADMIN)
 // --------------------------
 @Override
 public UsuarioResponseDTO buscarPorEmail(String email) {
     Usuario usuario = usuarioRepository.findByEmail(email)
             .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
     return UsuarioMapperManual.toResponseDTO(usuario);
 }

 // --------------------------
 // Actualizar usuario
 // --------------------------
 @Override
 public UsuarioResponseDTO actualizarUsuario(String email, RegistroUsuarioDTO dto) {

     Usuario usuario = usuarioRepository.findByEmail(email)
             .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

     usuario.setNombre(dto.getNombre());
     usuario.setTelefono(dto.getTelefono());

     if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
         usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
     }

     if (dto.getRolId() != null) {
         Rol rol = rolRepository.findById(dto.getRolId())
                 .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
         usuario.setRol(rol);
     }

     if (dto.getSedeId() != null) {
         Sede sede = sedeRepository.findById(dto.getSedeId())
                 .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
         usuario.setSede(sede);
     }

     usuarioRepository.save(usuario);
     return UsuarioMapperManual.toResponseDTO(usuario);
 }

 // --------------------------
 // Eliminar usuario
 // --------------------------
 @Override
 public void eliminarUsuario(String email) {
     Usuario usuario = usuarioRepository.findByEmail(email)
             .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
     usuarioRepository.delete(usuario);
 }

    

}