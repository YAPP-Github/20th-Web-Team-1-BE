package com.yapp.betree.service.oauth;

import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.dto.oauth.JwtTokenDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    public static final String ISSUER = "BeTree";
    public static final int DAYS = 7;

    private final String secretKey;
    private final Long tokenValidMilliseconds;
    private final Long refreshTokenValidMilliseconds;

    @Autowired
    public JwtTokenProvider(@Value("${secrets.jwt.token.secret-key}") String secretKey,
                            @Value("${secrets.jwt.token.expiration-time}") Long tokenValidMilliseconds,
                            @Value("${secrets.jwt.token.refresh-expiration-time}") Long refreshTokenValidMilliseconds) {
        this.secretKey = secretKey;
        this.tokenValidMilliseconds = tokenValidMilliseconds; // 1시간
        this.refreshTokenValidMilliseconds = refreshTokenValidMilliseconds * DAYS; // 하루 * days
    }

    public JwtTokenDto createToken(LoginUserDto user) {
        return JwtTokenDto.builder()
                .accessToken(createAccessToken(user))
                .refreshToken(createRefreshToken())
                .build();
    }

    String createAccessToken(LoginUserDto user) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(ISSUER)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidMilliseconds))
                .claim("id", user.getId())
                .claim("nickname", user.getNickname())
                .claim("email", user.getEmail())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(ISSUER)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidMilliseconds))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new BetreeException(ErrorCode.USER_TOKEN_EXPIRED, "token = " + token);
        } catch (JwtException e) {
            throw new BetreeException(ErrorCode.USER_TOKEN_ERROR, "token = " + token);
        }
    }
}
