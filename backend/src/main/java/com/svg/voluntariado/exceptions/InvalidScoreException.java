package com.svg.voluntariado.exceptions;

public class InvalidScoreException extends RuntimeException {
    public InvalidScoreException(String message) { super(message); }

    public InvalidScoreException() { super("A nota deve ser maior do que 0 (zero) e menor que 5 (cinco)."); }
}
