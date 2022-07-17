package com.yapp.betree.dto.oauth;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FruitType;
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
        Folder folder = Folder.builder()
                .fruit(FruitType.DEFAULT)
                .level(0L)
                .name("DEFAULT")
                .build();

        User user = User.builder()
                .oauthId(id)
                .nickname(nickname)
                .email(email)
                .userImage(BetreeUtils.makeUserImageNumber())
                .lastAccessTime(LocalDateTime.now())
                .url(BetreeUtils.makeUserAccessUrl(this))
                .build();
        user.addFolder(folder);
        return user;
    }
}
