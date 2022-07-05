package com.yapp.betree.service;

import com.yapp.betree.domain.RefreshToken;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.SendUserDto;
import com.yapp.betree.dto.response.UserResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.repository.RefreshTokenRepository;
import com.yapp.betree.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.yapp.betree.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    Optional<User> findByOauthId(Long oauthId) {
        log.info("oauthId로 유저 조회 : oauthId = {}", oauthId);
        return userRepository.findByOauthId(oauthId);
    }

    Optional<User> findById(Long userId) {
        log.info("userId로 유저 조회 : userId = {}", userId);
        return userRepository.findById(userId);
    }

    public SendUserDto findBySenderId(Long userId) {
        log.info("senderId로 유저 조회 : userId = {}", userId);
        if (userId == -1L) {
            return SendUserDto.ofNoLogin();
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new BetreeException(USER_NOT_FOUND, "senderId = " + userId));
        return SendUserDto.of(user);
    }

    @Transactional
    User save(User user) {
        log.info("User 등록 : {}", user);
        return userRepository.save(user);
    }

    @Transactional
    void saveRefreshToken(String token, Long userId) {
        Optional<RefreshToken> byUserId = refreshTokenRepository.findByUserId(userId);
        if (!byUserId.isPresent()) {
            log.info("refreshToken 생성 : token = {}, userId = {}", token, userId);
            RefreshToken refreshToken = RefreshToken.builder()
                    .token(token)
                    .userId(userId)
                    .build();
            refreshTokenRepository.save(refreshToken);
            return;
        }
        log.info("refreshToken 갱신 : token = {}, userId = {}", token, userId);
        RefreshToken refreshToken = byUserId.get();
        refreshToken.updateToken(token);
    }

    boolean isValidRefreshToken(String token, Long userId) {
        log.info("id로 refreshToken 조회 : userId = {}", userId);
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new BetreeException(ErrorCode.USER_REFRESH_ERROR, "userId = " + userId));
        return refreshToken.isSame(token);
    }

    public boolean isExist(Long userId) {
        return findById(userId).isPresent();
    }

    /**
     * 유저 정보 조회
     *
     * @param userId
     * @return
     */
    public UserResponseDto getUser(Long userId) {
        User user = findById(userId).orElseThrow(() -> new BetreeException(USER_NOT_FOUND, "userId = " + userId));
        return UserResponseDto.of(user);
    }

    /**
     * 유저 닉네임 변경
     *
     * @param userId
     * @param nickname
     */
    public void updateUserNickname(Long userId, String nickname) {
        findById(userId).orElseThrow(() -> new BetreeException(USER_NOT_FOUND, "userId = " + userId))
                .updateNickname(nickname);
    }
}
