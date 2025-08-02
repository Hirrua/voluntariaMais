package com.svg.voluntariado.exceptions;

public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException(String message) {
        super(message);
    }

    public UserUnauthorizedException() { super("O usuário não possui autorização."); }
}
