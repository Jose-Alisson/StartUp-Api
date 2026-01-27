package br.start.up.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ResponseHttpHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> httpHandler(ResponseStatusException exception){
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
    }
}
