package com.proyecto.shared.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String mensaje) {
        super(mensaje);
    }
}
