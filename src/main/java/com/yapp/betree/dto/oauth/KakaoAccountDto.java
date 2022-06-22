package com.yapp.betree.dto.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoAccountDto {
    @JsonProperty("profile")
    private KakaoProfileDto profile;

    @JsonProperty("email")
    private String email;
}
