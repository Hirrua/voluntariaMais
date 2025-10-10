package com.svg.voluntariado.exceptions;

public class UserPendingConfirmationException extends RuntimeException {

    public UserPendingConfirmationException() {
        super("Usuário ainda não confirmou o registro. Verifique o e-mail enviado para conclusão.");
    }
}
