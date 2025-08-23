package com.svg.voluntariado.exceptions;

public class FilledSubscriptionException extends RuntimeException {
    public FilledSubscriptionException(String message) {
        super(message);
    }

    public FilledSubscriptionException() {
      super("A vaga para esta atividade acaba de ser preenchida.");
  }
}
