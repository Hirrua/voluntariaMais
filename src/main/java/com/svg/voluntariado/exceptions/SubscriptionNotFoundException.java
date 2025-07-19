package com.svg.voluntariado.exceptions;

public class SubscriptionNotFoundException extends RuntimeException {
    public SubscriptionNotFoundException(String message) {
        super(message);
    }

    public SubscriptionNotFoundException() {
        super("Inscrição não encontrada.");
    }
}
