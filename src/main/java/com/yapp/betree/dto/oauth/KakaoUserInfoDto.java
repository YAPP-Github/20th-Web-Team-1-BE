package com.yapp.betree.dto.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
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
            throw new IllegalStateException("카카오 유저 정보 중 email은 null일 수 없습니다."); // TODO 예외처리
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
