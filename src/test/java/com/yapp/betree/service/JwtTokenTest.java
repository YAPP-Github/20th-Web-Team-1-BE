package com.yapp.betree.service;

import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.dto.oauth.JwtTokenDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.service.oauth.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.yapp.betree.domain.UserTest.TEST_SAVE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("Jwt 토큰 관련 테스트")
public class JwtTokenTest {

    public static final String JWT_TOKEN_TEST = Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .signWith(SignatureAlgorithm.HS256, "secretKey")
            .claim("id", "1")
            .claim("nickname", "닉네임")
            .claim("email", "email@email.com")
            .compact();

    public static final JwtTokenProvider JWT_PROVIDER = new JwtTokenProvider("secretKey", 600000L, 864000000L);


    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = JWT_PROVIDER;
    }

    @Test
    @DisplayName("유저 엔티티를 이용해서 jwt토큰을 생성한다.")
    void createTokenTest() {
        // given
        JwtTokenDto tokenDto = jwtTokenProvider.createToken(LoginUserDto.of(TEST_SAVE_USER));

        // when
        Claims accessTokenClaims = jwtTokenProvider.parseToken(tokenDto.getAccessToken());

        // then
        assertThat(String.valueOf(accessTokenClaims.get("id"))).isEqualTo(String.valueOf(TEST_SAVE_USER.getId()));
        assertThat(accessTokenClaims.get("nickname")).isEqualTo(TEST_SAVE_USER.getNickname());
        assertThat(accessTokenClaims.get("email")).isEqualTo(TEST_SAVE_USER.getEmail());
    }

    @Test
    @DisplayName("jwt 토큰 만료시 예외가 발생한다.")
    void expiredTokenTest() {
        jwtTokenProvider = new JwtTokenProvider("secretKey", 0L, 0L);
        JwtTokenDto tokenDto = jwtTokenProvider.createToken(LoginUserDto.of(TEST_SAVE_USER));

        assertThatThrownBy(() -> jwtTokenProvider.parseToken(tokenDto.getAccessToken()))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("만료")
                .extracting("code").isEqualTo(ErrorCode.USER_TOKEN_EXPIRED);

        assertThatThrownBy(() -> jwtTokenProvider.parseToken(tokenDto.getRefreshToken()))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("만료")
                .extracting("code").isEqualTo(ErrorCode.USER_TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("JWT 파싱시 기타 예외처리 테스트 - secretKey가 다를 경우 예외가 발생한다.")
    void jwtExceptionTest() {
        // given
        JwtTokenDto tokenDto = jwtTokenProvider.createToken(LoginUserDto.of(TEST_SAVE_USER));

        // when
        jwtTokenProvider = new JwtTokenProvider("anotherKey", 0L, 0L);

        // then
        assertThatThrownBy(() -> jwtTokenProvider.parseToken(tokenDto.getAccessToken()))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("토큰 파싱에 실패했습니다.")
                .extracting("code").isEqualTo(ErrorCode.USER_TOKEN_ERROR);
    }
}
