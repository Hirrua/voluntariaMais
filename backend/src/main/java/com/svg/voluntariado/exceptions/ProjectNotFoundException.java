package com.svg.voluntariado.exceptions;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String message) {
        super(message);
    }

    public ProjectNotFoundException() {
        super("Projeto não encontrado.");
    }
}
