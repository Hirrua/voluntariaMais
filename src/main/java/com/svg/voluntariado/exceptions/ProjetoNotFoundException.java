package com.svg.voluntariado.exceptions;

public class ProjetoNotFoundException extends RuntimeException {
    public ProjetoNotFoundException(String message) {
        super(message);
    }

    public ProjetoNotFoundException() {
        super("Projeto não encontrado.");
    }
}
