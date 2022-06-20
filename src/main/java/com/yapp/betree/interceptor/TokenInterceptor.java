package com.yapp.betree.interceptor;

import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.service.oauth.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    public TokenInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authType = "Bearer";
        String authHeader = Optional.ofNullable(request.getHeader("Authorization"))
                .orElseThrow(() -> new BetreeException(ErrorCode.USER_TOKEN_ERROR, "헤더에 토큰이 존재하지 않습니다."));

        if (authHeader.startsWith(authType)) {
            authHeader = authHeader.substring(authType.length()).trim();
        }

        Claims claims = jwtTokenProvider.parseToken(authHeader);
        if (Objects.isNull(claims)) {
            throw new BetreeException(ErrorCode.USER_TOKEN_ERROR, "토큰의 payload는 null일 수 없습니다.");
        }
        log.info("[토큰 검증 성공] claims: {}", claims);
        return true;
    }
}
