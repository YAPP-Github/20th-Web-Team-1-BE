package com.yapp.betree.service;

import com.yapp.betree.domain.User;
import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.dto.oauth.JwtTokenDto;
import com.yapp.betree.dto.oauth.OAuthUserInfoDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.service.oauth.JwtTokenProvider;
import com.yapp.betree.service.oauth.KakaoApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LoginService {

    private final KakaoApiService kakaoApiService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public JwtTokenDto createToken(String accessToken) {
        // 1. 토큰 유효성 검증 및 oAuthId 획득
        Long oauthId = kakaoApiService.getOauthId(accessToken);

        // 2. 회원가입 유무 확인 및 로그인 유저 생성
        Optional<User> loginUser = userService.findByOauthId(oauthId);
        if (!loginUser.isPresent()) {
            // 2-1. 카카오 API를 통해 회원가입할 유저 정보 요청
            OAuthUserInfoDto oAuthUserInfoDto = kakaoApiService.getUserInfo(accessToken);
            // 2-2. DB에 유저 등록(회원가입)하며 로그인 유저 생성
            loginUser = Optional.of(userService.save(oAuthUserInfoDto.generateSignUpUser()));
        }

        JwtTokenDto token = jwtTokenProvider.createToken(LoginUserDto.of(loginUser.get()));
        userService.saveRefreshToken(token.getRefreshToken(), loginUser.get().getId());
        return token;
    }

    @Transactional
    public JwtTokenDto refreshToken(String refreshToken) {
        // 1. 파싱 (유효성 체크)
        log.info("refreshToken 파싱 시도 token: {}", refreshToken);
        Long userId = jwtTokenProvider.parseRefreshToken(refreshToken);
        log.info("refreshToken 파싱 성공 token: {}, id: {}", refreshToken, userId);

        // 2. DB와 값 비교
        if (!userService.isValidRefreshToken(refreshToken, userId)) {
            throw new BetreeException(ErrorCode.USER_REFRESH_ERROR, "userId = " + userId);
        }
        User user = userService.findById(userId).orElseThrow(() -> new BetreeException(ErrorCode.USER_NOT_FOUND, "userId = " + userId));
        return jwtTokenProvider.refreshToken(LoginUserDto.of(user), refreshToken);
    }
}
