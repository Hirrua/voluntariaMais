package com.svg.voluntariado.exceptions;

public class SubscriptionConfirmedException extends RuntimeException {
    public SubscriptionConfirmedException(String message) {
        super(message);
    }

  public SubscriptionConfirmedException() {
    super("A inscrição já foi confirmada.");
  }
}
