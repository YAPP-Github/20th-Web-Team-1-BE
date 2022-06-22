package com.yapp.betree.dto;

import com.yapp.betree.dto.oauth.KakaoAccountDto;
import com.yapp.betree.dto.oauth.KakaoProfileDto;
import com.yapp.betree.dto.oauth.KakaoUserInfoDto;
import com.yapp.betree.dto.oauth.OAuthUserInfoDto;

public class UserInfoFixture {
    private static final Long ID = 1L;
    private static final String NICKNAME = "닉네임";
    private static final String EMAIL = "email@email.com";

    public static OAuthUserInfoDto createOAuthUserInfo() {
        return OAuthUserInfoDto.builder()
                .id(ID)
                .nickname(NICKNAME)
                .email(EMAIL)
                .build();
    }

    public static KakaoUserInfoDto createKakaoUserInfoResponse() {
        return KakaoUserInfoDto.builder()
                .id(ID)
                .kakaoAccount(new KakaoAccountDto(new KakaoProfileDto(NICKNAME), EMAIL))
                .build();
    }

    public static KakaoUserInfoDto createKakaoUserInfoFailResponse() {
        return KakaoUserInfoDto.builder()
                .id(ID)
                .kakaoAccount(new KakaoAccountDto(new KakaoProfileDto(NICKNAME), null))
                .build();
    }
}
