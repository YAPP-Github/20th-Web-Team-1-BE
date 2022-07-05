package com.yapp.betree.domain;

import com.yapp.betree.dto.SendUserDto;
import com.yapp.betree.dto.response.MessageResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.yapp.betree.domain.FolderTest.TEST_SAVE_DEFAULT_TREE;
import static com.yapp.betree.domain.UserTest.TEST_SAVE_USER;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("메시지 도메인, DTO 테스트")
public class MessageTest {

    public static Message TEST_SAVE_MESSAGE = Message.builder()
            .id(1L)
            .content("메시지내용")
            .senderId(TEST_SAVE_USER.getId())
            .user(TEST_SAVE_USER) // 받은사람
            .folder(TEST_SAVE_DEFAULT_TREE)
            .anonymous(false)
            .alreadyRead(false)
            .favorite(true)
            .opening(true)
            .build();

    public static Message TEST_SAVE_ANONYMOUS_MESSAGE = Message.builder()
            .id(1L)
            .content("메시지내용")
            .senderId(TEST_SAVE_USER.getId())
            .user(TEST_SAVE_USER) // 받은사람
            .folder(TEST_SAVE_DEFAULT_TREE)
            .anonymous(true)
            .alreadyRead(false)
            .favorite(true)
            .opening(false)
            .build();

    public static Message TEST_SAVE_DELETE_MESSAGE = Message.builder()
            .id(1L)
            .content("메시지내용")
            .senderId(TEST_SAVE_USER.getId())
            .user(TEST_SAVE_USER) // 받은사람
            .folder(TEST_SAVE_DEFAULT_TREE)
            .anonymous(true)
            .alreadyRead(false)
            .favorite(true)
            .opening(false)
            .delByReceiver(true)
            .build();

    @DisplayName("MessageResponseDto Set으로 중복 처리 되는지 테스트")
    @Test
    void messageResponseDtoSetTest() {
        Set<MessageResponseDto> dtos = new HashSet<>();
        SendUserDto sender = SendUserDto.builder()
                .id(TEST_SAVE_USER.getId())
                .nickname(TEST_SAVE_USER.getNickname())
                .userImage(TEST_SAVE_USER.getUserImage())
                .build();
        dtos.add(MessageResponseDto.of(TEST_SAVE_MESSAGE, sender));
        dtos.add(MessageResponseDto.of(TEST_SAVE_MESSAGE, sender));
        dtos.add(MessageResponseDto.of(TEST_SAVE_ANONYMOUS_MESSAGE, sender));
        assertThat(dtos).hasSize(2);
    }

}
