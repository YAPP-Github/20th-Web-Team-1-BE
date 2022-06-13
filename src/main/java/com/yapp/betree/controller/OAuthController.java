package com.yapp.betree.controller;

import com.yapp.betree.dto.oauth.OAuthUserInfoDto;
import com.yapp.betree.service.oauth.KakaoApiService;
import io.swagger.annotations.Api;
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
    @GetMapping("/api/signin")
    public ResponseEntity<OAuthUserInfoDto> oauthTest(@RequestHeader("X-Kakao-Access-Token") String accessToken) {
        log.info("카카오 로그인 요청 accessToken: {}", accessToken);

        if (!kakaoApiService.supports(accessToken)) {
            throw new IllegalArgumentException("토큰이 만료되었습니다. accessToken: " + accessToken);
        }

        OAuthUserInfoDto oAuthUserInfoDto = kakaoApiService.getUserInfo(accessToken);
        log.info("카카오 유저 정보 : {}", oAuthUserInfoDto);

        // TODO jwt토큰 발급
        return ResponseEntity.ok(oAuthUserInfoDto);
    }
}
