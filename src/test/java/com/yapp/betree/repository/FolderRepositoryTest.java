package com.yapp.betree.repository;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.User;
import com.yapp.betree.domain.UserTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Folder Repository 테스트")
public class FolderRepositoryTest {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("유저와 폴더 함께 생성")
    void folderUserSaveTest() {
        User user = UserTest.TEST_USER;
        Folder folder = Folder.builder()
                .fruit(FruitType.DEFAULT)
                .level(0L)
                .name("DEFAULT")
                .build();
        user.addFolder(folder);
        userRepository.save(user);

        List<Folder> folders = folderRepository.findAll();
        assertThat(folders).hasSize(1);

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
    }
}


