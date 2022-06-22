package com.yapp.betree.dto.request;

import com.yapp.betree.domain.FruitType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TreeRequestDto {

    private String name;
    private FruitType fruitType;

    @Builder
    public TreeRequestDto(String name, FruitType fruitType) {
        this.name = name;
        this.fruitType = fruitType;
    }
}
