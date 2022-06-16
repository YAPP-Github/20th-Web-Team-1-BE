package com.yapp.betree.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ForestResponseDto {

    private boolean hasNext;
    private List<TreeResponseDto> trees;

    @Builder
    public ForestResponseDto(boolean hasNext, List<TreeResponseDto> trees) {
        this.hasNext = hasNext;
        this.trees = trees;
    }
}
