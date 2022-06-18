package com.yapp.betree.dto.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoUserInfoDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccountDto kakaoAccount;

    public OAuthUserInfoDto buildUserInfo() {
        if (Objects.isNull(kakaoAccount.getEmail())) {
            throw new BetreeException(ErrorCode.OAUTH_INVALID_USERINFO, "Kakao email is null");
        }
        return OAuthUserInfoDto.builder()
                .id(id)
                .nickname(kakaoAccount.getProfile().getNickname())
                .email(kakaoAccount.getEmail())
                .build();
    }

    @Builder
    public KakaoUserInfoDto(Long id, KakaoAccountDto kakaoAccount) {
        this.id = id;
        this.kakaoAccount = kakaoAccount;
    }
}
