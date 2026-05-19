package com.example.assesement.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", ex.getMessage());
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.badRequest().body(error);
    }
}
