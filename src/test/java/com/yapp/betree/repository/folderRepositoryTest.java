package com.yapp.betree.repository;

import com.yapp.betree.domain.FruitType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.yapp.betree.domain.FolderTest.*;
import static com.yapp.betree.domain.UserTest.TEST_SAVE_USER;
import static com.yapp.betree.domain.UserTest.TEST_USER;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("folderRepository 테스트")
public class folderRepositoryTest {

    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("countByUserIdAndFruitIsNot 테스트")
    void countByUserIdAndFruitIsNot() {
        // given
        TEST_USER.addFolder(TEST_APPLE_TREE);
        TEST_USER.addFolder(TEST_DEFAULT_TREE);
        userRepository.save(TEST_USER);

        //when
        Long count = folderRepository.countByUserIdAndFruitIsNot(TEST_SAVE_USER.getId(), FruitType.DEFAULT);

        // then
        assertThat(count).isEqualTo(1); //기본폴더 제외 유저 폴더 개수
    }
}
