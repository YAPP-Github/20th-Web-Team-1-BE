package com.yapp.betree.service;

import com.yapp.betree.domain.User;
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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

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
}
