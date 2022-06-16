package com.yapp.betree.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private int status;
    private String code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ValidationError> errors;

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class ValidationError {
        private final String field;
        private final String value;
        private final String message;

        public static ValidationError of(FieldError fieldError) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .value(String.valueOf(fieldError.getRejectedValue()))
                    .message(fieldError.getDefaultMessage())
                    .build();
        }

        public static ValidationError of(ConstraintViolation violation) {
            return ValidationError.builder()
                    .field(String.valueOf(violation.getPropertyPath()))
                    .value(String.valueOf(violation.getInvalidValue()))
                    .message(violation.getMessageTemplate())
                    .build();
        }
    }

    @Builder
    public ErrorResponse(int status, String code, String message, List<ValidationError> errors) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.errors = errors;
    }
}
