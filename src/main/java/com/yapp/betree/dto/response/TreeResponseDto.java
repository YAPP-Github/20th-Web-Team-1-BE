package com.yapp.betree.dto.response;

import com.yapp.betree.domain.Folder;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TreeResponseDto {

    private Long id;
    private String name;

    @Builder
    public TreeResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static TreeResponseDto of(Folder folder) {
        return TreeResponseDto.builder()
                .id(folder.getId())
                .name(folder.getName())
                .build();
    }
}
