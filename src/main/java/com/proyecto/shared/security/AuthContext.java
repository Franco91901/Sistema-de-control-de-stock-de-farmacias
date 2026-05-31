package com.proyecto.shared.security;

import com.proyecto.auth.domain.model.Usuario;
import com.proyecto.shared.exception.EntityNotFoundException;
import com.proyecto.shared.exception.ExceptionConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthContext {

    public Usuario getUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Usuario usuario)) {
            throw new EntityNotFoundException(ExceptionConstants.USUARIO_NO_ENCONTRADO);
        }
        return usuario;
    }

    public Long getIdSede() {
        Usuario usuario = getUsuario();
        if (usuario.getSede() == null) {
            throw new IllegalStateException("El usuario no tiene sede asignada");
        }
        return usuario.getSede().getIdSede();
    }

    public Long getIdUsuario() {
        return getUsuario().getIdUsuario();
    }
}
