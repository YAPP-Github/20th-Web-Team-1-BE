package com.yapp.betree.dto.response;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FruitType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TreeResponseDto {

    private Long id;
    private String name;
    private FruitType fruit;

    @Builder
    public TreeResponseDto(Long id, String name, FruitType fruit) {
        this.id = id;
        this.name = name;
        this.fruit = fruit;
    }

    public static TreeResponseDto of(Folder folder) {
        return TreeResponseDto.builder()
                .id(folder.getId())
                .name(folder.getName())
                .fruit(folder.getFruit())
                .build();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        TreeResponseDto objDto = (TreeResponseDto) obj;
        if (this.id != objDto.getId()) {
            return false;
        }
        if (!this.name.equals(objDto.getName())) {
            return false;
        }
        if (this.fruit != objDto.getFruit()) {
            return false;
        }
        return true;
    }
}
