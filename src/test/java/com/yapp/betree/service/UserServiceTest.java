package com.yapp.betree.service;

import com.yapp.betree.domain.User;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.repository.RefreshTokenRepository;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.yapp.betree.domain.UserTest.TEST_SAVE_USER;
import static com.yapp.betree.domain.UserTest.TEST_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("oauthId를 통해 User를 얻어낼 수 있다.")
    void findByOauthIdTest() {
        // given
        given(userRepository.findByOauthId(TEST_USER.getOauthId())).willReturn(Optional.of(TEST_SAVE_USER));

        // when
        Optional<User> byOauthId = userService.findByOauthId(TEST_USER.getOauthId());

        // then
        assertThat(byOauthId).isPresent();
        assertThat(byOauthId.get().getId()).isEqualTo(TEST_SAVE_USER.getId());
    }

    @Test
    @DisplayName("존재하지 않는 oauthId로 조회 테스트")
    void findByOauthIdFailTest() {
        // given
        given(userRepository.findByOauthId(TEST_USER.getOauthId())).willReturn(Optional.empty());

        // when
        Optional<User> byOauthId = userService.findByOauthId(TEST_USER.getOauthId());

        // then
        assertThat(byOauthId).isEmpty();
    }

    @Test
    @DisplayName("isExist 테스트 - 존재하는 userId는 true를 반환한다.")
    void isExistTrueTest() {
        // given
        Long userId = 1L;
        given(userService.findById(userId)).willReturn(Optional.of(TEST_SAVE_USER));

        // when
        boolean exist = userService.isExist(userId);

        // then
        assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("isExist 테스트 - 존재하지 않는 userId는 false를 반환한다.")
    void isExistFalseTest() {
        // given
        Long userId = 1L;
        given(userService.findById(userId)).willReturn(Optional.empty());

        // when
        boolean exist = userService.isExist(userId);

        // then
        assertThat(exist).isFalse();
    }

    @Test
    @DisplayName("로그아웃 테스트 - 이미 로그아웃된 유저 처리")
    void logoutUserTest() {
        Long userId = 1L;
        given(refreshTokenRepository.findByUserId(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteRefreshToken(userId))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("이미 로그아웃된 유저입니다.")
                .extracting("code").isEqualTo(ErrorCode.USER_ALREADY_LOGOUT_TOKEN);
                
    }
    @Test
    @DisplayName("유저 정보 조회 테스트 - 존재하지 않는 userId로 조회시 예외처리")
    void findByIdFailTest() {
        // given
        given(userService.findById(TEST_SAVE_USER.getId())).willThrow(new BetreeException(ErrorCode.USER_NOT_FOUND));

        // then
        assertThatThrownBy(() -> userService.getUser(TEST_SAVE_USER.getId()))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다.")
                .extracting("code").isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("유저 닉네임 변경 테스트 - 존재하지 않는 userId로 조회시 예외처리")
    void updateUserNicknameFailTest() {
        // given
        given(userService.findById(TEST_SAVE_USER.getId())).willThrow(new BetreeException(ErrorCode.USER_NOT_FOUND));

        // then
        assertThatThrownBy(() -> userService.updateUserNickname(TEST_SAVE_USER.getId(), "nickname"))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다.")
                .extracting("code").isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}
