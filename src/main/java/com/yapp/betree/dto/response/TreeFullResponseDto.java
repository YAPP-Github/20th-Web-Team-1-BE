package com.yapp.betree.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TreeFullResponseDto {

    private Long id;
    private String name;
    private Long level;
    private Long prevId;
    private Long nextId;
    private List<MessageResponseDto> messages;

    @Builder
    public TreeFullResponseDto(Long id, String name, Long level, Long prevId, Long nextId, List<MessageResponseDto> messages) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.prevId = prevId;
        this.nextId = nextId;
        this.messages = messages;
    }
}
