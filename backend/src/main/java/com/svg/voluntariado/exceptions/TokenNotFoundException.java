package com.svg.voluntariado.exceptions;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String message) {
        super(message);
    }

    public TokenNotFoundException() {
        super("Token de confirmação não encontrado.");
    }
}
