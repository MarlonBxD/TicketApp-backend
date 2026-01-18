package com.marlondev.ticket_backend.infrastructure.dto.response;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DefaultResponse<T> {
    private boolean error;
    private String message;
    private HttpStatus httpStatus;
    private int httpCode;
    private T body;
}