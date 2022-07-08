package com.yapp.betree.interceptor;

import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.service.oauth.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.swagger.models.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    private static final String AUTH_TYPE = "Bearer";
    public static final String USER_ATTR_KEY = "user";
    public static final int ZERO = 0;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 비로그인 유저 요청가능 API는 return true
        if (isPassRequest(request)) {
            log.info("[비로그인 가능 요청] 토큰 검증 과정 생략");
            return true;
        }
        String authHeader = Optional.ofNullable(request.getHeader("Authorization"))
                .orElseThrow(() -> new BetreeException(ErrorCode.USER_TOKEN_ERROR, "헤더에 토큰이 존재하지 않습니다."));

        if (isInvalidRefreshToken(request.getCookies())) {
            if (request.getRequestURI().equals("/api/logout")) {
                throw new BetreeException(ErrorCode.USER_ALREADY_LOGOUT_TOKEN);
            }
            response.sendError(ErrorCode.USER_REFRESH_ERROR.getStatus(), ErrorCode.USER_REFRESH_ERROR.getMessage());
            return false;
        }

        if (authHeader.startsWith(AUTH_TYPE)) {
            authHeader = authHeader.substring(AUTH_TYPE.length()).trim();
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

    private boolean isInvalidRefreshToken(Cookie[] cookies) {
        if (Objects.isNull(cookies)) {
            return true;
        }
        Cookie refreshToken = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .findAny()
                .get();
        return Objects.isNull(refreshToken) || Objects.isNull(refreshToken.getValue()) || refreshToken.getValue().equals("") || refreshToken.getValue().isEmpty() || refreshToken.getMaxAge() == ZERO;
    }

    private boolean isPassRequest(HttpServletRequest request) {
        // 나무숲 조회, 나무숲 상세조회
        if (request.getRequestURI().startsWith("/api/forest") && request.getMethod().equals(String.valueOf(HttpMethod.GET))) {
            return true;
        }
        // 물주기
        if (request.getRequestURI().equals("/api/messages") && request.getMethod().equals(String.valueOf(HttpMethod.POST))) {

            Optional<String> authHeader = Optional.ofNullable(request.getHeader("Authorization"));

            if (!authHeader.isPresent()) { // 헤더에 토큰 없는, 비로그인 유저이면 처리 아니면 토큰파싱
                request.setAttribute(USER_ATTR_KEY, LoginUserDto.builder()
                        .id(Long.parseLong(String.valueOf(-1L))) // 비로그인 유저는 id -1로 설정하고 id == -1이면 뒤에서 처리 (userId임)
                        .nickname(request.getRemoteAddr()) // IP로 지정
                        .email(null)
                        .build()
                );
                return true;
            }
        }
        return false;
    }
}
