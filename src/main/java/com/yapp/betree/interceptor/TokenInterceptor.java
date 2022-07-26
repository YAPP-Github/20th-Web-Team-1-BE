package com.yapp.betree.interceptor;

import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.service.oauth.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.swagger.models.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    @Value("dev.email.js")
    private String jisu;
    @Value("dev.email.sb")
    private String subin;
    @Value("dev.email.si")
    private String sooim;
    @Value("dev.email.ym")
    private String ym;
    @Value("dev.email.yk")
    private String yk;

    private static final String AUTH_TYPE = "Bearer";
    public static final String USER_ATTR_KEY = "user";
    public static final int ZERO = 0;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if ("OPTIONS".equals(request.getMethod())) {
            log.info("CORS preflight 요청시 true 반환");
            return true;
        }

        printUserLog(request);

        // 비로그인 유저 요청가능 API는 return true
        if (isPassRequest(request)) {
            log.info("[비로그인 가능 요청] 토큰 검증 과정 생략");
            return true;
        }
        String authHeader = Optional.ofNullable(request.getHeader("Authorization"))
                .orElseThrow(() -> new BetreeException(ErrorCode.USER_TOKEN_ERROR, "헤더에 토큰이 존재하지 않습니다."));


        if (!isFrontEndLocal(authHeader) && isInvalidRefreshToken(request.getCookies())) {
            log.info("[리프레시토큰 검증] 비어있으면 실패");
            if (request.getRequestURI().equals("/api/logout")) {
                log.info("[리프레시토큰 검증] 이미로그아웃");
                throw new BetreeException(ErrorCode.USER_ALREADY_LOGOUT_TOKEN);
            }
            log.info("[리프레시토큰 검증] 예외생성");
            response.sendError(ErrorCode.USER_REFRESH_ERROR.getStatus(), ErrorCode.USER_REFRESH_ERROR.getMessage());
            return false;
        }


        log.info("[리프레시토큰 검증] 성공");
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

    private void printUserLog(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        String forward = request.getHeader("X-Forwarded-For");
        String proto = request.getHeader("X-Forwarded-Proto");
        log.info("[요청 유저 정보] ip:{}, forward:{}, proto:{}", ip, forward, proto);
    }

    private boolean isFrontEndLocal(String authHeader) {
        // FE, BE 개발자들 로그인할때는 토큰검증 임시로 예외처리->로컬개발 가능하게 하려고
        List<String> devEmails = new ArrayList<>();
        devEmails.add(jisu);
        devEmails.add(subin);
        devEmails.add(yk);
        devEmails.add(ym);
        devEmails.add(sooim);

        authHeader = authHeader.substring(AUTH_TYPE.length()).trim();
        Claims claims = jwtTokenProvider.parseToken(authHeader);
        if (Objects.isNull(claims)) {
            throw new BetreeException(ErrorCode.USER_TOKEN_ERROR, "토큰의 payload는 null일 수 없습니다.");
        }

        log.info("[개발자 로그인] info : {}", claims.get("email"));
        return devEmails.contains(claims.get("email"));
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

        // 나무숲 상세조회 & 물주기 & 나무 숲 조회
        if (request.getRequestURI().equals("/api/messages") && request.getMethod().equals(String.valueOf(HttpMethod.POST)) ||
                request.getRequestURI().startsWith("/api/forest") && request.getMethod().equals(String.valueOf(HttpMethod.GET))) {

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
