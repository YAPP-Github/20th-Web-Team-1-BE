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
public class LoginUserDto {
    private Long id;
    private String nickname;
    private String email;

    @Builder
    public LoginUserDto(Long id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }

    /**
     * Jwt 토큰 발급을 위한 로그인 유저 DTO
     *
     * @param user
     * @return LoginUserDto
     */
    public static LoginUserDto of(User user) {
        return LoginUserDto.builder()
                .id(user.getOauthId())
                .nickname(user.getNickName())
                .email(user.getEmail())
                .build();
    }
}