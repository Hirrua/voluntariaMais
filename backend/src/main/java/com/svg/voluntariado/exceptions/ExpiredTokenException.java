package com.svg.voluntariado.exceptions;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException(String message) {
        super(message);
    }

    public ExpiredTokenException() {
        super("Token expirado.");
    }
}
