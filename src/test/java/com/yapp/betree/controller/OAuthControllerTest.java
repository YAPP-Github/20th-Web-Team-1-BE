package com.yapp.betree.controller;

import com.yapp.betree.dto.oauth.JwtTokenDto;
import com.yapp.betree.service.LoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.reactive.function.client.WebClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("OAuth 컨트롤러 테스트")
@WebMvcTest(OAuthController.class)
public class OAuthControllerTest extends ControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    @Test
    @DisplayName("MockMvc는 null이 아니다.")
    void mvcIsNotNull() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @DisplayName("회원가입,로그인 - 헤더에 AccessToken이 없으면 예외가 발생한다.")
    void headerAccessTokenNullTest() throws Exception {
        mockMvc.perform(get("/api/signin"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("C001"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입,로그인 - OAuth API 요청에 실패하면 예외가 발생한다. ")
    void oauthUserInfoInvalidTest() throws Exception {
        String accessToken = "accessToken";
        given(loginService.createToken(accessToken)).willThrow(new KakaoWebClientException("401"));

        mockMvc.perform(get("/api/signin")
                .header("X-Kakao-Access-Token", accessToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value("O000"))
                .andDo(print());
    }

    private static class KakaoWebClientException extends WebClientException {
        public KakaoWebClientException(String msg) {
            super(msg);
        }
    }

    @Test
    @DisplayName("회원가입, 로그인 - 성공하면 헤더를 통해 토큰이 발급된다.")
    void signAndCreateTokenTest() throws Exception {
        String accessToken = "accessToken";
        given(loginService.createToken(accessToken)).willReturn(JwtTokenDto.builder()
                .accessToken("betreeAccessToken")
                .refreshToken("betreeRefreshToken")
                .build());

        MvcResult mvcResult = mockMvc.perform(get("/api/signin")
                .header("X-Kakao-Access-Token", accessToken))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        assertThat(mvcResult.getResponse().getCookie("refreshToken").getValue()).isEqualTo("betreeRefreshToken");
        assertThat(mvcResult.getResponse().getHeader("Authorization")).isEqualTo("Bearer betreeAccessToken");
    }
}