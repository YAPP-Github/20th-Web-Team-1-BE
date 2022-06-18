package com.yapp.betree.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientException;

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

    // header에 required 필드에 값이 들어오지 않은 경우
    @ExceptionHandler(value = MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(Exception e) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse er = getErrorResponse(e.getMessage(), errorCode);
        log.error("MissingRequestHeaderExcpetion[{}]", er);
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(er);
    }

    // WebClient를 이용한 외부 API요청에 예외가 발생한 경우
    @ExceptionHandler(value = WebClientException.class)
    public ResponseEntity<ErrorResponse> handleWebClientException(WebClientException e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse er = getErrorResponse(e.getMessage(), errorCode);
        log.error("WebClientExcpetion[{}]", er);
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