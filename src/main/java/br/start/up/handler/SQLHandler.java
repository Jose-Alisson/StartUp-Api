package br.start.up.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class SQLHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        if (message.contains("email")) {
            errorResponse.put("email", "o email já esta em uso");
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
