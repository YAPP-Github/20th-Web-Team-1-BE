package com.yapp.betree.controller;

import com.yapp.betree.annotation.LoginUser;
import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.dto.oauth.JwtTokenDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;

@Api
@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    public static final String SET_COOKIE_HEADER = "Set-Cookie";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTH_TYPE = "Bearer ";
    public static final String COOKIE_REFRESH_TOKEN = "refreshToken";

    private final LoginService loginService;

    /**
     * 카카오 OAuth이용한 로그인 [토큰검증 - 토큰에서 사용자 정보 획득(+회원가입) - JWT토큰 발급]
     *
     * @param accessToken
     * @return
     */
    @ApiOperation(value = "OAuth 인증", notes = "카카오에서 받아온 토큰으로 로그인(회원가입)\n" +
            "요청 완료시 쿠키(HttpOnly)로 리프레시 토큰, 헤더(Authorization)로 Bearer accessToken이 발급됩니다.")
    @ApiResponses({
            @ApiResponse(code = 400, message = "[C001]Invalid input value(Required request header is not present)"),
            @ApiResponse(code = 401, message = "[O000]OAuth 서버와의 연동에 실패했습니다.(kakao api 실패, 토큰 만료 등)\n" +
                    "[O001]OAuth로 받아온 유저정보가 올바르지 않습니다. - 이메일 누락 등 \n" +
                    "[O002]OAuth로 받아온 액세스 토큰이 만료되었습니다.\n"),
    })
    @PostMapping("/api/signin")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> signIn(@RequestHeader("X-Kakao-Access-Token") String accessToken, HttpServletResponse response) {
        log.info("회원 로그인 요청 accessToken: {}", accessToken);

        JwtTokenDto token = loginService.createToken(accessToken);
        return buildTokenResponse(response, token);
    }

    @ApiOperation(value = "토큰 재발급", notes = "RefreshToken으로 AccessToken 재발급\n" +
            "요청 완료시 쿠키(HttpOnly)로 리프레시 토큰, 헤더(Authorization)로 Bearer accessToken이 발급됩니다.")
    @ApiResponses({
            @ApiResponse(code = 401, message = "[U003]JWT 리프레시 토큰이 만료되었습니다. 재로그인이 필요합니다.\n" +
                    "[U004]유효하지 않은 JWT 리프레시 토큰입니다. 재로그인이 필요합니다.\n"),
            @ApiResponse(code = 404, message = "[U005]회원을 찾을 수 없습니다.")
    })
    @PostMapping("/api/refresh-token")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> refreshToken(
            HttpServletRequest request, HttpServletResponse response) {
        if (Objects.isNull(request.getCookies())) {
            throw new BetreeException(ErrorCode.USER_REFRESH_ERROR, "쿠키에 토큰이 존재하지 않습니다.");
        }

        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> COOKIE_REFRESH_TOKEN.equals(cookie.getName()))
                .findAny()
                .orElseThrow(() -> new BetreeException(ErrorCode.USER_REFRESH_ERROR, "쿠키에 토큰이 존재하지 않습니다."))
                .getValue();

        log.info("회원 토큰 재발급 요청 refreshToken: {}", refreshToken);
        JwtTokenDto token = loginService.refreshToken(refreshToken);
        return buildTokenResponse(response, token);
    }

    private ResponseEntity<Void> buildTokenResponse(HttpServletResponse response, JwtTokenDto token) {
        log.info("JWT 토큰 발급 : {}", token);

        ResponseCookie cookie = ResponseCookie.from(COOKIE_REFRESH_TOKEN, token.getRefreshToken())
                .maxAge(24 * 60 * 60 * 7)
                .path("/")
                .secure(true)
//                .sameSite("None")
                .httpOnly(true)
                .build();
        response.setHeader(SET_COOKIE_HEADER, cookie.toString());
        response.setHeader(AUTHORIZATION_HEADER, AUTH_TYPE + token.getAccessToken());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @ApiOperation(value = "로그아웃", notes = "로그아웃, refresh Token 만료")
    @ApiResponses({
            @ApiResponse(code = 200, message = "[U007]이미 로그아웃된 유저입니다.\n")
    })
    @PostMapping("/api/logout")
    public ResponseEntity<Void> logout(@ApiIgnore @LoginUser LoginUserDto loginUser, HttpServletResponse response) {
        log.info("[로그아웃 요청] user : {}", loginUser);
        loginService.logout(loginUser.getId());

        ResponseCookie cookie = ResponseCookie.from(COOKIE_REFRESH_TOKEN, null)
                .maxAge(0)
                .path("/")
                .secure(true)
//                .sameSite("None")
                .httpOnly(true)
                .build();
        response.setHeader(SET_COOKIE_HEADER, cookie.toString());
        return ResponseEntity.ok().build();
    }
}
