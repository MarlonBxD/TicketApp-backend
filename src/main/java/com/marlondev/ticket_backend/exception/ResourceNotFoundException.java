package com.marlondev.ticket_backend.exception;

import com.marlondev.ticket_backend.infrastructure.dto.response.DefaultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ResourceNotFoundException extends RuntimeException {


    public ResourceNotFoundException(String message) {
        super(message);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<DefaultResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        DefaultResponse<Void> response = DefaultResponse.<Void>builder()
                .error(true)
                .message(ex.getMessage())
                .httpStatus(HttpStatus.NOT_FOUND)
                .httpCode(HttpStatus.NOT_FOUND.value())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
