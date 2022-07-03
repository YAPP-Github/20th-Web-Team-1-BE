package com.yapp.betree.repository;

import com.yapp.betree.domain.FolderTest;
import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.User;
import com.yapp.betree.domain.UserTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("메시지 레파지토리 테스트")
public class MessageRepositoryTest {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @DisplayName("즐겨찾기한 메시지 조회 테스트")
    @Test
    void findByFavoriteTest() {
        User user = UserTest.TEST_SAVE_USER;
        user.addFolder(FolderTest.TEST_DEFAULT_TREE);
        userRepository.save(user);

        Message message = Message.builder()
                .content("안녕")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(true)
                .favorite(true)
                .opening(false)
                .user(user)
                .build();
        messageRepository.save(message);

        Message message1 = Message.builder()
                .content("안녕")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(true)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();
        messageRepository.save(message1);

        List<Message> messages = messageRepository.findAllByUserIdAndFavorite(user.getId(), true);
        assertThat(messages).hasSize(1);
    }
}
