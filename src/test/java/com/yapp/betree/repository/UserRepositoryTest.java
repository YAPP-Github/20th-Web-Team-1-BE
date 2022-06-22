package com.yapp.betree.repository;

import com.yapp.betree.domain.User;
import com.yapp.betree.dto.UserInfoFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("UserRepository 테스트")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByOauthId 테스트")
    void findByOauthIdTest() {
        // given
        User user = UserInfoFixture.createOAuthUserInfo().generateSignUpUser();
        // when
        userRepository.save(user);

        // then
        Optional<User> userByOauthId = userRepository.findByOauthId(user.getOauthId());
        assertThat(userByOauthId).isPresent();
        assertThat(userByOauthId.get().getNickName()).isEqualTo(user.getNickName());
    }
}
