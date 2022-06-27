package com.yapp.betree.dto;

import com.yapp.betree.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SendUserDto {

    private Long id;
    private String nickname;
    private String userImage;

    @Builder
    public SendUserDto(Long id, String nickname, String userImage) {
        this.id = id;
        this.nickname = nickname;
        this.userImage = userImage;
    }

    public static SendUserDto of(User user) {
        return SendUserDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .userImage(user.getUserImage())
                .build();
    }

    public static SendUserDto ofNoLogin() {
        return SendUserDto.builder()
                .id(-1L)
                .nickname("익명")
                .userImage("기본이미지")
                .build();
    }
}
