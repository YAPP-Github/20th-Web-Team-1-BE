package com.yapp.betree.controller;

import com.yapp.betree.dto.oauth.JwtTokenDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Api
@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final LoginService loginService;

    /**
     * 카카오 OAuth이용한 로그인 [토큰검증 - 토큰에서 사용자 정보 획득(+회원가입) - JWT토큰 발급]
     *
     * @param accessToken
     * @return
     */
    @ApiOperation(value = "OAuth 인증", notes = "카카오에서 받아온 토큰으로 로그인(회원가입\n" +
            "요청 완료시 쿠키(HttpOnly)로 리프레시 토큰, 헤더(Authorization)로 Bearer accessToken이 발급됩니다.")
    @ApiResponses({
            @ApiResponse(code = 400, message = "[C001]Invalid input value(Required request header is not present)"),
            @ApiResponse(code = 401, message = "[O000]OAuth 서버와의 연동에 실패했습니다.(kakao api 실패, 토큰 만료 등)\n" +
                    "[O001]OAuth로 받아온 유저정보가 올바르지 않습니다. - 이메일 누락 등 \n" +
                    "[O002]OAuth로 받아온 액세스 토큰이 만료되었습니다.\n"),
    })
    @GetMapping("/api/signin")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> signIn(@RequestHeader("X-Kakao-Access-Token") String accessToken, HttpServletResponse response) {
        log.info("회원 로그인 요청 accessToken: {}", accessToken);

        JwtTokenDto token = loginService.createToken(accessToken);
        log.info("JWT 토큰 발급 : {}", token);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                .maxAge(24 * 60 * 60 * 7)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
        response.setHeader("Authorization", "Bearer " + token.getAccessToken());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
