package com.svg.voluntariado.exceptions;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException() {
        super("Perfil do voluntário não foi encontrado.");
    }
}
