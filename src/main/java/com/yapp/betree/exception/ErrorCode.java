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

    //OAuth
    OAUTH_SERVER_ERROR(401, "O000", "OAuth 서버와의 연동에 실패했습니다."),
    OAUTH_INVALID_USERINFO(401, "O001", "OAuth로 받아온 유저정보가 올바르지 않습니다."),
    OAUTH_ACCESS_TOKEN_EXPIRED(401, "O002", "OAuth로 받아온 액세스 토큰이 만료되었습니다."),

    // User
    USER_TOKEN_EXPIRED(401, "U001","JWT 토큰이 만료되었습니다. 재발급이 필요합니다."),
    USER_TOKEN_ERROR(401,"U002","JWT 토큰 파싱에 실패했습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
