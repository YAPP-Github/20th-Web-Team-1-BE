package com.yapp.betree.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "C001", "Invalid input value"),
    INTERNAL_SERVER_ERROR(500, "C002", "Internal server error"),
    METHOD_NOT_ALLOWED(405, "C003", "Method not allowed"),
    ;

    private final int status;
    private final String code;
    private final String message;
}
