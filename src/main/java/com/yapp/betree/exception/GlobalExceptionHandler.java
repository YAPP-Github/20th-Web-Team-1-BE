package com.yapp.betree.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = BetreeException.class)
    public ResponseEntity<ErrorResponse> handleBetreeException(BetreeException e) {
        ErrorResponse er = getErrorResponse(e.getMessage(), e.getCode());
        log.error("handleBetreeException[{}]", er);
        return ResponseEntity
                .status(e.getCode().getStatus())
                .body(er);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse er = getErrorResponse(e, errorCode);
        log.error("handleValidationException[{}]", er);
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(er);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse er = getErrorResponse(e, errorCode);
        log.error("ConstraintViolationException[{}]", er);
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(er);
    }

    public static ErrorResponse getErrorResponse(String message, ErrorCode code) {
        return ErrorResponse.builder()
                .code(code.getCode())
                .message(message)
                .status(code.getStatus())
                .build();
    }

    public static ErrorResponse getErrorResponse(BindException e, ErrorCode code) {

        List<ErrorResponse.ValidationError> validationErrorList = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ErrorResponse.ValidationError::of)
                .collect(Collectors.toList());

        return ErrorResponse.builder()
                .code(code.getCode())
                .message(code.getMessage())
                .errors(validationErrorList)
                .status(code.getStatus())
                .build();
    }

    public static ErrorResponse getErrorResponse(ConstraintViolationException e, ErrorCode code) {

        List<ErrorResponse.ValidationError> validationErrorList = e.getConstraintViolations()
                .stream()
                .map(ErrorResponse.ValidationError::of)
                .collect(Collectors.toList());

        return ErrorResponse.builder()
                .code(code.getCode())
                .message(code.getMessage())
                .errors(validationErrorList)
                .status(code.getStatus())
                .build();
    }
}