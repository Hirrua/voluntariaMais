package com.svg.voluntariado.exceptions;

public class OngNotFoundException extends RuntimeException {

    public OngNotFoundException(String message) {
        super(message);
    }

    public OngNotFoundException() { super("Ong não encontrada."); }
}
