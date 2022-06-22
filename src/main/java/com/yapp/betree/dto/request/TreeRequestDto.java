package com.yapp.betree.dto.request;

import com.yapp.betree.domain.FruitType;
import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(description = "나무추가 요청 DTO")
public class TreeRequestDto {

    @NotBlank(message = "나무 이름은 빈값일 수 없습니다.")
    private String name;

    private FruitType fruitType;

    @Builder
    public TreeRequestDto(String name, FruitType fruitType) {
        this.name = name;
        this.fruitType = fruitType;
    }
}