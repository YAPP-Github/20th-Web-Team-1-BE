package com.yapp.betree.interceptor;

import com.yapp.betree.dto.LoginUserDto;
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

    public static final String USER_ATTR_KEY = "user";
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

        if (claims.containsKey("id")) { // @LoginUser 생성 위함 -> 테스트할때는 claims가 {}라서 제외
            request.setAttribute(USER_ATTR_KEY, LoginUserDto.builder()
                    .id(Long.parseLong(String.valueOf(claims.get("id"))))
                    .nickname((String) claims.get("nickname"))
                    .email((String) claims.get("email"))
                    .build()
            );
        }
        return true;
    }
}
