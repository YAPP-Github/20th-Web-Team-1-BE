package com.yapp.betree.dto.response;

import com.yapp.betree.domain.Folder;
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
    public TreeFullResponseDto(Folder folder, Long prevId, Long nextId, List<MessageResponseDto> messages) {
        this.id = folder.getId();
        this.name = folder.getName();
        this.level =folder.getLevel();
        this.prevId = prevId;
        this.nextId = nextId;
        this.messages = messages;
    }
}
