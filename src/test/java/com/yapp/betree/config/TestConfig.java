package com.yapp.betree.config;

import com.yapp.betree.service.oauth.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

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

    public static Claims getClaims(Long userId) {
        return new Claims() {
            @Override
            public String getIssuer() {
                return null;
            }

            @Override
            public Claims setIssuer(String iss) {
                return null;
            }

            @Override
            public String getSubject() {
                return null;
            }

            @Override
            public Claims setSubject(String sub) {
                return null;
            }

            @Override
            public String getAudience() {
                return null;
            }

            @Override
            public Claims setAudience(String aud) {
                return null;
            }

            @Override
            public Date getExpiration() {
                return null;
            }

            @Override
            public Claims setExpiration(Date exp) {
                return null;
            }

            @Override
            public Date getNotBefore() {
                return null;
            }

            @Override
            public Claims setNotBefore(Date nbf) {
                return null;
            }

            @Override
            public Date getIssuedAt() {
                return null;
            }

            @Override
            public Claims setIssuedAt(Date iat) {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public Claims setId(String jti) {
                return null;
            }

            @Override
            public <T> T get(String claimName, Class<T> requiredType) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return true;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public Object get(Object key) {
                String keyStr = (String) key;
                if (keyStr.equals("id")) {
                    return userId;
                }
                if (keyStr.equals("nickname")) {
                    return "닉네임";
                }
                if (keyStr.equals("email")) {
                    return "이메일";
                }
                return null;
            }

            @Override
            public Object put(String key, Object value) {
                return null;
            }

            @Override
            public Object remove(Object key) {
                return null;
            }

            @Override
            public void putAll(Map<? extends String, ?> m) {

            }

            @Override
            public void clear() {

            }

            @Override
            public Set<String> keySet() {
                return null;
            }

            @Override
            public Collection<Object> values() {
                return null;
            }

            @Override
            public Set<Entry<String, Object>> entrySet() {
                return null;
            }
        };
    }
}
