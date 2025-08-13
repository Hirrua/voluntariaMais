package com.svg.voluntariado.exceptions;

public class InvalidDateException extends RuntimeException {
    public InvalidDateException(String message) { super(message); }

    public InvalidDateException() { super("As datas não são válidas."); }
}
