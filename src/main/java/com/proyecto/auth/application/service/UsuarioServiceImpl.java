package com.proyecto.auth.application.service;

import com.proyecto.auth.application.dto.response.UsuarioResponseDTO;
import com.proyecto.auth.application.dto.request.RegisterRequestDTO;
import com.proyecto.auth.application.mapper.UsuarioMapper;
import com.proyecto.auth.domain.model.Rol;
import com.proyecto.auth.domain.model.Usuario;
import com.proyecto.auth.domain.repository.RolRepository;
import com.proyecto.auth.domain.repository.UsuarioRepository;
import com.proyecto.core.sede.domain.model.Sede;
import com.proyecto.core.sede.domain.repository.SedeRepository;
import com.proyecto.shared.exception.EntityNotFoundException;
import com.proyecto.shared.exception.ExceptionConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final SedeRepository sedeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponseDTO crearUsuario(RegisterRequestDTO dto) {
        Rol rol = dto.rolId() != null
                ? rolRepository.findById(dto.rolId())
                        .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.ROL_NO_ENCONTRADO))
                : rolRepository.findByNombre("FARMACEUTICO")
                        .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.ROL_NO_ENCONTRADO));

        Sede sede = null;
        if (dto.sedeId() != null) {
            sede = sedeRepository.findById(dto.sedeId())
                    .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.SEDE_NO_ENCONTRADA));
        }

        Usuario usuario = UsuarioMapper.fromRegistroDTO(dto, rol, sede);
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuarioRepository.save(usuario);
        return UsuarioMapper.toResponseDTO(usuario);
    }

    @Override
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UsuarioResponseDTO obtenerPorEmail(String email) {
        return UsuarioMapper.toResponseDTO(findOrThrow(email));
    }

    @Override
    public UsuarioResponseDTO buscarPorEmail(String email) {
        return obtenerPorEmail(email);
    }

    @Override
    public UsuarioResponseDTO actualizarUsuario(String email, RegisterRequestDTO dto) {
        Usuario usuario = findOrThrow(email);
        usuario.setNombre(dto.nombre());
        usuario.setApellido(dto.apellido());
        usuario.setTelefono(dto.telefono());
        usuario.setDni(dto.dni());
        if (dto.password() != null && !dto.password().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.password()));
        }
        if (dto.rolId() != null) {
            usuario.setRol(rolRepository.findById(dto.rolId())
                    .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.ROL_NO_ENCONTRADO)));
        }
        if (dto.sedeId() != null) {
            usuario.setSede(sedeRepository.findById(dto.sedeId())
                    .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.SEDE_NO_ENCONTRADA)));
        }
        usuarioRepository.save(usuario);
        return UsuarioMapper.toResponseDTO(usuario);
    }

    @Override
    public void eliminarUsuario(String email) {
        usuarioRepository.delete(findOrThrow(email));
    }

    @Override
    public UsuarioResponseDTO aprobarUsuario(String email) {
        Usuario usuario = findOrThrow(email);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        return UsuarioMapper.toResponseDTO(usuario);
    }

    @Override
    public List<UsuarioResponseDTO> listarPendientes() {
        return usuarioRepository.findByActivoFalse().stream()
                .map(UsuarioMapper::toResponseDTO).toList();
    }

    private Usuario findOrThrow(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionConstants.USUARIO_NO_ENCONTRADO));
    }
}
