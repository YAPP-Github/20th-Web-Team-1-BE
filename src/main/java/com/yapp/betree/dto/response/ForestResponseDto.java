package com.yapp.betree.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ForestResponseDto {

    private Long entireTreeSize;
    private List<TreeResponseDto> trees;

    @Builder
    public ForestResponseDto(Long entireTreeSize, List<TreeResponseDto> trees) {
        this.entireTreeSize = entireTreeSize;
        this.trees = trees;
    }
}
