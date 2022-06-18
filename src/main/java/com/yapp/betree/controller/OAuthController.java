package com.yapp.betree.controller;

import com.yapp.betree.dto.oauth.OAuthUserInfoDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.service.oauth.KakaoApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final KakaoApiService kakaoApiService;

    /**
     * 카카오 OAuth이용한 로그인 [토큰검증 - 토큰에서 사용자 정보 획득(+회원가입) - JWT토큰 발급]
     *
     * @param accessToken
     * @return
     */
    @ApiOperation(value = "OAuth 인증", notes = "카카오에서 받아온 토큰으로 로그인(회원가입)")
    @ApiResponses({
            @ApiResponse(code = 400, message = "[C001]Invalid input value(Required request header is not present)"),
            @ApiResponse(code = 401, message = "[O000]OAuth 서버와의 연동에 실패했습니다.(kakao api 실패, 토큰 만료 등)\n" +
                    "[O001]OAuth로 받아온 유저정보가 올바르지 않습니다. - 이메일 누락 등 \n" +
                    "[O002]OAuth로 받아온 액세스 토큰이 만료되었습니다.\n"),
    })
    @GetMapping("/api/signin")
    public ResponseEntity<OAuthUserInfoDto> oauthTest(@RequestHeader("X-Kakao-Access-Token") String accessToken) {
        log.info("카카오 로그인 요청 accessToken: {}", accessToken);

        if (!kakaoApiService.supports(accessToken)) {
            throw new BetreeException(ErrorCode.OAUTH_ACCESS_TOKEN_EXPIRED, "accessToken = " + accessToken);
        }

        OAuthUserInfoDto oAuthUserInfoDto = kakaoApiService.getUserInfo(accessToken);
        log.info("카카오 유저 정보 : {}", oAuthUserInfoDto);

        // TODO jwt토큰 발급
        return ResponseEntity.ok(oAuthUserInfoDto);
    }
}
