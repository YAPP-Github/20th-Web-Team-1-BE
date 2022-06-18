package com.yapp.betree.service.oauth;

import com.yapp.betree.dto.oauth.KakaoTokenInfoDto;
import com.yapp.betree.dto.oauth.KakaoUserInfoDto;
import com.yapp.betree.dto.oauth.OAuthUserInfoDto;
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
     *
     * @param accessToken
     * @return boolean
     */
    public boolean supports(String accessToken) {
        KakaoTokenInfoDto kakaoTokenInfoDto = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(KAKAO_TOKEN_INFO_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoTokenInfoDto.class)
                .block();

        return kakaoTokenInfoDto.getExpiresIn() > 0;
    }

    /**
     * 요청에서 받은 카카오 accessToken을 이용해서 카카오 API서버에서 token에 해당하는 유저 정보를 얻어온다.
     *
     * @param accessToken
     * @return OAuthUserInfoDto
     */
    public OAuthUserInfoDto getUserInfo(String accessToken) {
        KakaoUserInfoDto kakaoUserInfoDto = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(KAKAO_USER_INFO_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfoDto.class)
                .block();

        return kakaoUserInfoDto.buildUserInfo();
    }
}
