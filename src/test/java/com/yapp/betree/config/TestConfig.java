package com.yapp.betree.config;

import com.yapp.betree.service.oauth.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseCookie;

import javax.servlet.http.Cookie;

import static com.yapp.betree.controller.OAuthController.COOKIE_REFRESH_TOKEN;

@TestConfiguration
public class TestConfig {
    public static Cookie COOKIE_DELETE_TOKEN = new Cookie("refreshToken", null);
    public static Cookie COOKIE_TOKEN = new Cookie("refreshToken", "cookie");
    static {
        COOKIE_DELETE_TOKEN.setMaxAge(0);
        COOKIE_DELETE_TOKEN.setHttpOnly(true);
        COOKIE_TOKEN.setMaxAge(100);
        COOKIE_TOKEN.setHttpOnly(true);
    }
    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider("secretKey", 6000L, 6000L) {
            @Override
            public Claims parseToken(String token) {
                return Jwts.parser()
                        .setSigningKey("secretKey")
                        .parseClaimsJws(token)
                        .getBody();
            };
        };
    }
}
