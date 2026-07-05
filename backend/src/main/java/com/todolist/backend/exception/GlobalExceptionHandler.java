package com.todolist.backend.exception;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<HashMap<String, String>> handleException(RuntimeException exception) {
        HashMap<String, String> response = new HashMap<>();
        response.put("statusCode", HttpStatus.BAD_REQUEST.toString());
        response.put("exception", exception.getClass().getSimpleName());
        response.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
