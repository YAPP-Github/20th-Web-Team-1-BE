package com.yapp.betree.domain;

import com.yapp.betree.dto.SendUserDto;
import com.yapp.betree.util.BetreeUtils;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

@DisplayName("User 테스트")
public class UserTest {

    // save하기 전 테스트용 user (id는 null)
    public static final User TEST_USER = User.builder()
            .oauthId(1L)
            .nickname("닉네임")
            .email("email@email.com")
            .userImage("default image uri")
            .lastAccessTime(LocalDateTime.now())
            .url(BetreeUtils.makeUserAccessUrl(1L))
            .build();
    public static final User TEST_SAVE_USER = User.builder()
            .id(1L)
            .oauthId(1L)
            .nickname("닉네임")
            .email("email@email.com")
            .userImage("default image uri")
            .lastAccessTime(LocalDateTime.now())
            .url(BetreeUtils.makeUserAccessUrl(1L))
            .build();
    public static final SendUserDto TEST_SAVE_USER_DTO = SendUserDto.builder()
            .id(1L)
            .nickname("닉네임")
            .userImage("default image uri")
            .build();
}
