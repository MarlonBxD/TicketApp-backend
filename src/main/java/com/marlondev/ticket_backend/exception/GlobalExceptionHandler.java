package com.marlondev.ticket_backend.exception;

import com.marlondev.ticket_backend.infrastructure.dto.response.DefaultResponse;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@lombok.extern.slf4j.Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ResourceNotFoundException.class,
            EntityNotFoundException.class
    })
    public ResponseEntity<DefaultResponse<Void>> handleNotFound(RuntimeException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<DefaultResponse<Void>> handleBadRequest(BadRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            errors.put(fieldName, error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                DefaultResponse.<Map<String, String>>builder()
                        .error(true)
                        .message("Validation failed")
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .httpCode(HttpStatus.BAD_REQUEST.value())
                        .body(errors)
                        .build());
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            UsernameNotFoundException.class
    })
    public ResponseEntity<DefaultResponse<Void>> handleAuthenticationException(Exception ex) {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultResponse<Void>> handleGlobalException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error: " + ex.getMessage());
    }

    private <T> ResponseEntity<DefaultResponse<T>> buildResponse(
            HttpStatus status,
            String message) {
        return ResponseEntity.status(status).body(
                DefaultResponse.<T>builder()
                        .error(true)
                        .message(message)
                        .httpStatus(status)
                        .httpCode(status.value())
                        .build());
    }
}
