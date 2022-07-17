package com.yapp.betree.repository;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FolderTest;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.User;
import com.yapp.betree.domain.UserTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@DisplayName("메시지 레파지토리 테스트")
public class MessageRepositoryTest {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Disabled
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

        List<Message> messages = messageRepository.findAllByUserIdAndFavoriteAndDelByReceiver(user.getId(), true, false);
        assertThat(messages).hasSize(1);
    }

    @Disabled
    @DisplayName("메시지 삭제시 기본폴더로 변경 테스트")
    @Test
    void deleteMessageTest() {
        User user = UserTest.TEST_SAVE_USER;
        user.addFolder(FolderTest.TEST_DEFAULT_TREE);
        user.addFolder(FolderTest.TEST_APPLE_TREE);
        User save = userRepository.save(user);

        Folder folder = folderRepository.findByUserIdAndFruit(save.getId(), FruitType.APPLE);
        Folder defaultFolder = folderRepository.findByUserIdAndFruit(save.getId(), FruitType.DEFAULT);
        Message message = Message.builder()
                .content("안녕")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(true)
                .favorite(true)
                .opening(false)
                .user(user)
                .folder(folder)
                .build();
        messageRepository.save(message);

        List<Message> messages = messageRepository.findAllByUserIdAndFolderIdAndDelByReceiver(save.getId(), folder.getId(), false);
        Message delMessage = messages.get(0);
        //삭제하기 전
        assertThat(delMessage.isDelByReceiver()).isFalse();
        assertThat(delMessage.getFolder().getFruit()).isEqualTo(FruitType.APPLE);

        messages.stream()
                .forEach(m -> m.updateDeleteStatus(save.getId(), defaultFolder));

        // 삭제한 후
        Message byId = messageRepository.findById(delMessage.getId()).get();
        assertThat(byId.isDelByReceiver()).isTrue();
        assertThat(byId.getFolder().getFruit()).isEqualTo(FruitType.DEFAULT);
    }

    @DisplayName("이전 , 다음 메세지 조회 테스트")
    @Test
    void findPrevNextMessageTest() {
        User user = UserTest.TEST_USER;
        user.addFolder(FolderTest.TEST_DEFAULT_TREE);
        user.addFolder(FolderTest.TEST_APPLE_TREE);
        userRepository.save(user);

        Folder folder = folderRepository.findByUserIdAndFruit(user.getId(), FruitType.DEFAULT);
        Folder appleFolder = folderRepository.findByUserIdAndFruit(user.getId(), FruitType.APPLE);
        Message message1 = Message.builder()
                .content("안녕1")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(true)
                .favorite(true)
                .opening(false)
                .user(user)
                .folder(folder)
                .build();
        messageRepository.save(message1);
        Message messageApple1 = Message.builder()
                .content("안녕1")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(true)
                .favorite(true)
                .opening(false)
                .user(user)
                .folder(appleFolder)
                .build();
        messageRepository.save(messageApple1);

        Message message2 = Message.builder()
                .content("안녕2")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(true)
                .favorite(false)
                .opening(false)
                .user(user)
                .folder(folder)
                .build();
        messageRepository.save(message2);
        Message messageApple2 = Message.builder()
                .content("안녕1")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(true)
                .favorite(true)
                .opening(false)
                .user(user)
                .folder(appleFolder)
                .build();
        messageRepository.save(messageApple2);

        List<Message> all = messageRepository.findAll();
        assertThat(all).hasSize(4);
        Optional<Message> prevMessage = messageRepository.findTop1ByUserIdAndFolderIdAndIdLessThanOrderByIdDesc(user.getId(), message1.getId(), folder.getId());
        Optional<Message> nextMessage = messageRepository.findTop1ByUserIdAndFolderIdAndIdGreaterThan(user.getId(), message1.getId(), folder.getId());

        assertThatThrownBy(prevMessage::get).isInstanceOf(NoSuchElementException.class);
        assertThat(nextMessage.get().getId()).isEqualTo(message2.getId());
    }
}
