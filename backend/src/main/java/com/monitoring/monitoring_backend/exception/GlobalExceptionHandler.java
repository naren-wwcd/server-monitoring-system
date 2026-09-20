package com.monitoring.monitoring_backend.exception;

import com.monitoring.monitoring_backend.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleServerNotFound(ServerNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MetricNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMetricNotFound(MetricNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(RateLimitExceededException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.TOO_MANY_REQUESTS.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
    }
}