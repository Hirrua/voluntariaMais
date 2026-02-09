package com.svg.voluntariado.infra;

import com.svg.voluntariado.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.net.UnknownHostException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(OngNotFoundException.class)
    private ResponseEntity<RestErrorMessage> ongNotFoundHandler(OngNotFoundException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    private ResponseEntity<RestErrorMessage> projectNotFoundHandler(ProjectNotFoundException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(ActivityNotFoundException.class)
    private ResponseEntity<RestErrorMessage> activityNotFoundHandler(ActivityNotFoundException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    private ResponseEntity<RestErrorMessage> profileNotFoundHandler(ProfileNotFoundException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<RestErrorMessage> userNotFoundHandler(UserNotFoundException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(SubscriptionNotFoundException.class)
    private ResponseEntity<RestErrorMessage> subscriptionNotFoundHandler(SubscriptionNotFoundException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(FilledSubscriptionException.class)
    private ResponseEntity<RestErrorMessage> filledSubscriptionHandler(FilledSubscriptionException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionResponse);
    }

    @ExceptionHandler(ExpiredTokenException.class)
    private ResponseEntity<RestErrorMessage> expiredTokenHandler(ExpiredTokenException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(SubscriptionConfirmedException.class)
    private ResponseEntity<RestErrorMessage> subscriptionConfirmedHandler(SubscriptionConfirmedException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionResponse);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    private ResponseEntity<RestErrorMessage> tokenNotFoundHandler(TokenNotFoundException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(InvalidScoreException.class)
    private ResponseEntity<RestErrorMessage> invalidScoreHandler(InvalidScoreException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.NOT_ACCEPTABLE, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(exceptionResponse);
    }

    @ExceptionHandler(UserUnauthorizedException.class)
    private ResponseEntity<RestErrorMessage> userUnauthorizedHandler(UserUnauthorizedException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.UNAUTHORIZED, exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    @ExceptionHandler(InvalidDateException.class)
    private ResponseEntity<RestErrorMessage> invalidDataHandler(InvalidDateException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.NOT_ACCEPTABLE, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(exceptionResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    private ResponseEntity<RestErrorMessage> accessDeniedHandler(AccessDeniedException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.FORBIDDEN, "Acesso negado.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    @ExceptionHandler(S3Exception.class)
    private ResponseEntity<RestErrorMessage> storageHandler(S3Exception exception) {
        String message;
        if (exception.statusCode() == HttpStatus.UNAUTHORIZED.value()
                || exception.statusCode() == HttpStatus.FORBIDDEN.value()) {
            message = "Falha de autenticacao no armazenamento de arquivos. Verifique as credenciais/permissoes do bucket.";
        } else {
            message = "Falha ao comunicar com o armazenamento de arquivos.";
        }

        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.BAD_GATEWAY, message);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(exceptionResponse);
    }

    @ExceptionHandler(SdkClientException.class)
    private ResponseEntity<RestErrorMessage> sdkClientHandler(SdkClientException exception) {
        String message = "Falha de conectividade com o armazenamento de arquivos.";
        if (exception.getCause() instanceof UnknownHostException) {
            message = "Falha de DNS ao resolver o endpoint do armazenamento de arquivos.";
        }

        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.BAD_GATEWAY, message);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(exceptionResponse);
    }
}
