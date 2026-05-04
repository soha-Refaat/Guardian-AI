package com.example.GuardianAIDatabase.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,String>> handleNotFound(RuntimeException ex){
        Map<String,String> error = new HashMap<>();
        error.put("Error",ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>>handleGeneral(Exception ex){
        Map<String,String>error = new HashMap<>();
        error.put("Error",ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
