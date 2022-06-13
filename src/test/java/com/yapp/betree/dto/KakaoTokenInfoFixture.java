package com.yapp.betree.dto;

import com.yapp.betree.dto.oauth.KakaoTokenInfoDto;

public class KakaoTokenInfoFixture {
    private static final Long ID = 1L;
    private static final Integer EXPIRES_IN = 20000;
    private static final Integer APP_ID = 100;

    public static KakaoTokenInfoDto createKakaoTokenInfoResponse() {
        return KakaoTokenInfoDto.builder()
                .id(1L)
                .expiresIn(20000)
                .appId(1)
                .build();
    }

    public static KakaoTokenInfoDto createKakaoTokenInfoFailResponse() {
        return KakaoTokenInfoDto.builder()
                .id(1L)
                .expiresIn(0)
                .appId(1)
                .build();
    }
}
