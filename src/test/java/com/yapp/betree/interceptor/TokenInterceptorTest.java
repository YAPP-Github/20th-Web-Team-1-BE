package com.yapp.betree.interceptor;

import com.yapp.betree.domain.UserTest;
import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.dto.oauth.JwtTokenDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.service.JwtTokenTest;
import com.yapp.betree.service.oauth.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@DisplayName("토큰 검증 인터셉터 테스트")
public class TokenInterceptorTest {

    private TokenInterceptor tokenInterceptor;
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        this.jwtTokenProvider = JwtTokenTest.JWT_PROVIDER;
        this.tokenInterceptor = new TokenInterceptor(jwtTokenProvider);
    }

    @Test
    @DisplayName("헤더에 Authorization이 존재하지 않으면 예외가 발생한다")
    void headerFailTest() throws Exception {
        assertThatThrownBy(() ->
                tokenInterceptor.preHandle(new MockHttpServletRequest(), null, null))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("헤더에 토큰이 존재하지 않습니다.");

    }

    @Test
    @DisplayName("토큰 파싱에 실패하면 예외가 발생한다.")
    void headerTokenParseFailTest() {
        jwtTokenProvider = new JwtTokenProvider("anotherSecretKey", 0L, 0L);
        JwtTokenDto token = jwtTokenProvider.createToken(LoginUserDto.of(UserTest.TEST_SAVE_USER));

        assertThatThrownBy(() ->
                tokenInterceptor.preHandle(jwtAuthHttpRequest(token.getAccessToken()), null, null))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("토큰 파싱에 실패했습니다.");
    }

    @Test
    @DisplayName("토큰 검증 성공시 인터셉터가 true를 반환한다.")
    void claimAttributeTest() throws Exception {
        JwtTokenDto token = jwtTokenProvider.createToken(LoginUserDto.of(UserTest.TEST_SAVE_USER));

        assertThat(tokenInterceptor.preHandle(jwtAuthHttpRequest(token.getAccessToken()), null, null)).isTrue();
    }

    private MockHttpServletRequest jwtAuthHttpRequest(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return request;
    }
}
