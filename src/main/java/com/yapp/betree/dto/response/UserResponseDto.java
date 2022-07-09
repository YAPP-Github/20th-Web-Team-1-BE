package com.yapp.betree.dto.response;

import com.yapp.betree.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResponseDto {

    private Long id;
    private String nickname;
    private String email;
    private String url;
    private String userImage;

    @Builder
    public UserResponseDto(Long id, String nickname, String email, String url, String userImage) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.url = url;
        this.userImage = userImage;
    }

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .userImage(user.getUserImage())
                .url(user.getUrl())
                .build();
    }
}
