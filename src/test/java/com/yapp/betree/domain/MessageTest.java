package com.yapp.betree.domain;

import org.junit.jupiter.api.DisplayName;

import static com.yapp.betree.domain.UserTest.TEST_SAVE_USER;
import static com.yapp.betree.domain.FolderTest.TEST_SAVE_DEFAULT_TREE;

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

}
