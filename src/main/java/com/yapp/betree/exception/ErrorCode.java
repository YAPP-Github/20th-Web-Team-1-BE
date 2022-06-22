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
    ENUM_FORMAT_INVALID(400, "C004", "잘못된 ENUM값 입니다."),

    // Forest
    FOREST_PAGE_ERROR(400,"F001","페이지는 0 또는 1이어야 합니다."),
    FOREST_EMPTY_PAGE(400, "FOO2", "해당 페이지에 나무가 존재하지 않습니다."),

    // Tree
    TREE_NOT_FOUND(404,"T001", "나무가 존재하지 않습니다."),

    //OAuth
    OAUTH_SERVER_ERROR(401, "O000", "OAuth 서버와의 연동에 실패했습니다."),
    OAUTH_INVALID_USERINFO(401, "O001", "OAuth로 받아온 유저정보가 올바르지 않습니다."),
    OAUTH_ACCESS_TOKEN_EXPIRED(401, "O002", "OAuth로 받아온 액세스 토큰이 만료되었습니다."),

    // User
    USER_TOKEN_EXPIRED(401, "U001","JWT 토큰이 만료되었습니다. 재발급이 필요합니다."),
    USER_TOKEN_ERROR(401,"U002","JWT 토큰 파싱에 실패했습니다."),
    USER_REFRESH_TOKEN_EXPIRED(401,"U003", "JWT 리프레시 토큰이 만료되었습니다. 재로그인이 필요합니다."),
    USER_REFRESH_ERROR(401,"U004", "유효하지 않은 JWT 리프레시 토큰입니다. 재로그인이 필요합니다."),
    USER_NOT_FOUND(404, "U005", "회원을 찾을 수 없습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
