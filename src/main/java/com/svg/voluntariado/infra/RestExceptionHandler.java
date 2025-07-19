package com.svg.voluntariado.infra;

import com.svg.voluntariado.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
}
