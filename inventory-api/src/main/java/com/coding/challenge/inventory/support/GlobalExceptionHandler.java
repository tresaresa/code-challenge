package com.coding.challenge.inventory.support;

import com.coding.challenge.inventory.dto.response.BaseResponse;
import com.coding.challenge.inventory.dto.response.SimpleOperationResponse;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SimpleOperationResponse> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());
        return ResponseEntity.ok(new SimpleOperationResponse(BaseResponse.FAILED, "validation failed"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<SimpleOperationResponse> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        return ResponseEntity.ok(new SimpleOperationResponse(BaseResponse.FAILED, "constraint violation"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<SimpleOperationResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        log.warn("Unreadable request body: {}", ex.getMessage());
        return ResponseEntity.ok(new SimpleOperationResponse(BaseResponse.FAILED, "unreadable request body"));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<SimpleOperationResponse> handleDataAccess(DataAccessException ex) {
        log.error("Data access error", ex);
        return ResponseEntity.ok(new SimpleOperationResponse(BaseResponse.FAILED, "database error"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SimpleOperationResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.ok(new SimpleOperationResponse(BaseResponse.FAILED, "illegal argument"));
    }
}