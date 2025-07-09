package com.svg.voluntariado.infra;

import com.svg.voluntariado.exceptions.AtividadeNotFoundException;
import com.svg.voluntariado.exceptions.OngNotFoundException;
import com.svg.voluntariado.exceptions.ProjetoNotFoundException;
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

    @ExceptionHandler(ProjetoNotFoundException.class)
    private ResponseEntity<RestErrorMessage> projetoNotFoundHandler(ProjetoNotFoundException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(AtividadeNotFoundException.class)
    private ResponseEntity<RestErrorMessage> atividadeNotFoundHandler(AtividadeNotFoundException exception) {
        RestErrorMessage exceptionResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }
}
