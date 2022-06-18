package com.yapp.betree.service.oauth;

import com.yapp.betree.dto.oauth.KakaoTokenInfoDto;
import com.yapp.betree.dto.oauth.KakaoUserInfoDto;
import com.yapp.betree.dto.oauth.OAuthUserInfoDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class KakaoApiService {
    private static final String KAPI_BASE_URL = "https://kapi.kakao.com";
    private static final String KAKAO_TOKEN_INFO_URL = "/v1/user/access_token_info";
    private static final String KAKAO_USER_INFO_URL = "/v2/user/me";

    private final WebClient webClient;

    @Autowired
    public KakaoApiService() {
        this(KAPI_BASE_URL);
    }

    public KakaoApiService(String apiUrl) {
        this.webClient = initWebClient(apiUrl);
    }

    private WebClient initWebClient(String apiUrl) {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    /**
     * 요청에서 받은 카카오 accessToken을 이용해서 카카오 API서버에 토큰이 유효한지 검증한다.
     * 1. 토큰이 유효하지 않다면 API요청 실패
     * 2. 토큰이 유효하다면 oauthId 반환
     *
     * @param accessToken
     * @return Long oauthId
     */
    public Long getOauthId(String accessToken) {
        log.info("[카카오 API]토큰 유효성 확인 accessToken: {}", accessToken);
        KakaoTokenInfoDto kakaoTokenInfoDto = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(KAKAO_TOKEN_INFO_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoTokenInfoDto.class)
                .block();

        if (kakaoTokenInfoDto.getExpiresIn() <= 0) {
            throw new BetreeException(ErrorCode.OAUTH_ACCESS_TOKEN_EXPIRED, "accessToken = " + accessToken);
        }
        return kakaoTokenInfoDto.getId();
    }

    /**
     * 요청에서 받은 카카오 accessToken을 이용해서 카카오 API서버에서 token에 해당하는 유저 정보를 얻어온다.
     *
     * @param accessToken
     * @return OAuthUserInfoDto
     */
    public OAuthUserInfoDto getUserInfo(String accessToken) {
        log.info("[카카오 API]유저 정보 요청 accessToken: {}", accessToken);
        KakaoUserInfoDto kakaoUserInfoDto = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(KAKAO_USER_INFO_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfoDto.class)
                .block();
        log.info("[카카오 API]유저 정보 획득 accessToken: {}, userInfoDto: {}", accessToken, kakaoUserInfoDto);
        return kakaoUserInfoDto.buildUserInfo();
    }
}
