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

    public static final String ERROR_MESSAGE_DELIMITER = ": ";
    public static final String OAUTH_UNAUTHORIZED_CODE = "401";


    @ExceptionHandler(value = BetreeException.class)
    public ResponseEntity<ErrorResponse> handleBetreeException(BetreeException e) {
        ErrorResponse er = getErrorResponse(e.getMessage(), e.getCode());
        return getResponseEntity(e.getCode(), er, "handleBetreeException");
    }

    /**
     * 새로운 예외 핸들러 생성 방식
     * 1. 적절한 에러 코드 지정 - 주로 Common or 특정 예외 코드
     * 2. ErrorResponse 생성 - e.getMessage() 그대로 또는 적절하게 변형
     * 3. getResponseEntity(errorCode, er, " 예외 이름 ") 메서드를 이용해 반환
     *
     * @param e
     * @return ResponseEntity<ErrorResponse>
     */

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse er = getErrorResponse(e, errorCode);
        return getResponseEntity(errorCode, er, "handleValidationException");
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse er = getErrorResponse(e, errorCode);
        return getResponseEntity(errorCode, er, "handleConstraintViolationException");
    }

    // header에 required 필드에 값이 들어오지 않은 경우
    @ExceptionHandler(value = MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(Exception e) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse er = getErrorResponse(e.getMessage(), errorCode);
        return getResponseEntity(errorCode, er, "handleMissingRequestHeaderException");
    }

    // WebClient를 이용한 외부 API요청에 예외가 발생한 경우
    @ExceptionHandler(value = WebClientException.class)
    public ResponseEntity<ErrorResponse> handleWebClientException(WebClientException e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse er = getErrorResponse(e.getMessage(), errorCode);
        if (e.getMessage().contains(OAUTH_UNAUTHORIZED_CODE)) {
            errorCode = ErrorCode.OAUTH_SERVER_ERROR;
            er = getErrorResponse(errorCode.getMessage() + ERROR_MESSAGE_DELIMITER + e.getMessage(), errorCode);
        }
        return getResponseEntity(errorCode, er, "handleWebClientException");
    }

    /**
     * 공통 응답값 반환 메서드
     *
     * @param errorCode
     * @param er
     * @param exceptionName
     * @return ResponseEntity<ErrorResponse>
     */
    private ResponseEntity<ErrorResponse> getResponseEntity(ErrorCode errorCode, ErrorResponse er, String exceptionName) {
        log.error("{}[{}]", exceptionName, er);
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(er);
    }

    private ErrorResponse getErrorResponse(String message, ErrorCode code) {
        return ErrorResponse.builder()
                .code(code.getCode())
                .message(message)
                .status(code.getStatus())
                .build();
    }

    private ErrorResponse getErrorResponse(BindException e, ErrorCode code) {

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

    private ErrorResponse getErrorResponse(ConstraintViolationException e, ErrorCode code) {

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