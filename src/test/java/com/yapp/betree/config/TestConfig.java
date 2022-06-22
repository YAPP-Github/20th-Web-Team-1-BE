package com.yapp.betree.config;

import com.yapp.betree.service.oauth.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
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
