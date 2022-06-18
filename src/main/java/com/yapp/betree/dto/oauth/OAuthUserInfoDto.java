package com.yapp.betree.dto.oauth;

import com.yapp.betree.domain.User;
import com.yapp.betree.util.BetreeUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthUserInfoDto {
    private Long id;
    private String nickname;
    private String email;

    @Builder
    public OAuthUserInfoDto(Long id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }

    /**
     * 카카오 유저 정보로부터 최초 회원가입시 User 엔티티 생성 메서드
     *
     * @return
     */
    public User generateSignUpUser() {
        return User.builder()
                .oauthId(id)
                .nickName(nickname)
                .email(email)
                .userImage("default image uri") // TODO uri 결정
                .lastAccessTime(LocalDateTime.now())
                .url(BetreeUtils.makeUserAccessUrl(this))
                .build();
    }
}
