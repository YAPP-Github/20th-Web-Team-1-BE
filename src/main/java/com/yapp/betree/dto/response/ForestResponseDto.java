package com.yapp.betree.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ForestResponseDto {

    private String nickname;
    List<TreeResponseDto> responseDtoList;

    @Builder
    public ForestResponseDto(String nickname, List<TreeResponseDto> responseDtoList) {
        this.nickname = nickname;
        this.responseDtoList = responseDtoList;
    }
}
