package com.yapp.betree.dto.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoTokenInfoDto {
    @JsonProperty("id")
    @NotNull
    private Long id;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    @JsonProperty("app_id")
    private Integer appId;

    @Builder
    public KakaoTokenInfoDto(Long id, Integer expiresIn, Integer appId) {
        this.id = id;
        this.expiresIn = expiresIn;
        this.appId = appId;
    }
}
