//package com.rag.chat.storage.config;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
////@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    // Handle generic exceptions
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
//        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
//    }
//
//    // Handle IllegalArgumentException
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
//        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
//    }
//
//    // Handle validation errors (if using @Valid)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getFieldErrors().forEach(error ->
//                errors.put(error.getField(), error.getDefaultMessage())
//        );
//        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
//    }
//
//    // Custom response builder
//    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
//        Map<String, Object> body = new HashMap<>();
//        body.put("timestamp", LocalDateTime.now());
//        body.put("status", status.value());
//        body.put("error", status.getReasonPhrase());
//        body.put("message", message);
//        return new ResponseEntity<>(body, status);
//    }
//
//    private ResponseEntity<Object> buildResponse(HttpStatus status, String message, Map<String, String> details) {
//        Map<String, Object> body = new HashMap<>();
//        body.put("timestamp", LocalDateTime.now());
//        body.put("status", status.value());
//        body.put("error", status.getReasonPhrase());
//        body.put("message", message);
//        body.put("details", details);
//        return new ResponseEntity<>(body, status);
//    }
//}