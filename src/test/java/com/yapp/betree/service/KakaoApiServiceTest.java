package com.yapp.betree.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.yapp.betree.dto.oauth.KakaoTokenInfoDto;
import com.yapp.betree.dto.oauth.KakaoUserInfoDto;
import com.yapp.betree.dto.oauth.OAuthUserInfoDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.service.oauth.KakaoApiService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.io.IOException;

import static com.yapp.betree.dto.KakaoTokenInfoFixture.createKakaoTokenInfoFailResponse;
import static com.yapp.betree.dto.KakaoTokenInfoFixture.createKakaoTokenInfoResponse;
import static com.yapp.betree.dto.UserInfoFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("카카오 외부 api 요청 mock 테스트")
public class KakaoApiServiceTest {

    private static MockWebServer mockWebServer;
    private static ObjectMapper objectMapper;

    private KakaoApiService kakaoApiService;

    @BeforeAll
    static void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        mockWebServer = new MockWebServer();
        mockWebServer.play();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                mockWebServer.getPort());
        kakaoApiService = new KakaoApiService(baseUrl);
    }

    @Test
    @DisplayName("카카오 토큰 유효 검증 - expiresIn값이 양수이면 유효한 토큰이다.")
    void supportsTest() throws Exception {
        KakaoTokenInfoDto kakaoTokenInfoDto = createKakaoTokenInfoResponse();

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(kakaoTokenInfoDto))
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        assertThat(kakaoApiService.supports("accessToken")).isTrue();
    }

    @Test
    @DisplayName("카카오 토큰 유효 검증 - expiresIn값이 0이하이면 유효하지 않은 토큰이다.")
    void supportsFailTest() throws Exception {
        KakaoTokenInfoDto kakaoTokenInfoDto = createKakaoTokenInfoFailResponse();

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(kakaoTokenInfoDto))
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        assertThat(kakaoApiService.supports("accessToken")).isFalse();
    }

    @Test
    @DisplayName("카카오 유저 정보 요청 - 토큰으로 유저정보를 얻어올 수 있다.")
    void getUserInfoTest() throws Exception {
        KakaoUserInfoDto kakaoUserInfo = createKakaoUserInfoResponse();
        OAuthUserInfoDto oAuthUserInfo = createOAuthUserInfo();

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(kakaoUserInfo))
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));


        OAuthUserInfoDto userInfo = kakaoApiService.getUserInfo("accessToken");
        assertThat(userInfo.getId()).isEqualTo(oAuthUserInfo.getId());
        assertThat(userInfo.getNickname()).isEqualTo(oAuthUserInfo.getNickname());
        assertThat(userInfo.getEmail()).isEqualTo(oAuthUserInfo.getEmail());
    }

    @Test
    @DisplayName("카카오 유저 정보 요청 - 유저정보 이메일에 빈 값이 있으면 예외가 발생한다.")
    void getUserInfoFailTest() throws Exception {
        KakaoUserInfoDto kakaoUserInfo = createKakaoUserInfoFailResponse();
        OAuthUserInfoDto oAuthUserInfo = createOAuthUserInfo();

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(kakaoUserInfo))
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        assertThatThrownBy(() -> kakaoApiService.getUserInfo("accessToken"))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("email is null");
    }
}
