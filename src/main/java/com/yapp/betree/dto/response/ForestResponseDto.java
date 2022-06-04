package com.yapp.betree.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ForestResponseDto {

    private Long totalTreeSize;
    private List<TreeResponseDto> trees;

    @Builder
    public ForestResponseDto(Long totalTreeSize, List<TreeResponseDto> trees) {
        this.totalTreeSize = totalTreeSize;
        this.trees = trees;
    }
}
