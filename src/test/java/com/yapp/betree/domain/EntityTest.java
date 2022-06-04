package com.yapp.betree.domain;

import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@DisplayName("엔티티 테스트")
@SpringBootTest
public class EntityTest {

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    UserRepository userRepository;

    @Disabled
    @DisplayName("기본 DB 값 생성")
    @Test
    void createEntity() {

        User user = User.builder()
                .nickName("user")
                .email("user@user.com")
                .url("testUrl")
                .lastAccessTime(LocalDateTime.now())
                .userImage("123")
                .build();
        userRepository.save(user);

        Folder folder = Folder.builder()
                .fruit(FruitType.DEFAULT)
                .name("default")
                .user(user)
                .level(0L)
                .build();
        folderRepository.save(folder);

        List<User> all = userRepository.findAll();
        List<Folder> all1 = folderRepository.findAll();

        System.out.println(all);
        System.out.println(all1);
    }
}
