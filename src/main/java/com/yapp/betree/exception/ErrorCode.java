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

    // Forest
    FOREST_PAGE_ERROR(400,"F001","페이지는 0 또는 1이어야 합니다."),
    FOREST_EMPTY_PAGE(400, "FOO2", "해당 페이지에 나무가 존재하지 않습니다.")
    ;

    private final int status;
    private final String code;
    private final String message;
}
