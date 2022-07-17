package com.yapp.betree.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageDetailResponseDto {

    private MessageBoxResponseDto responseDto;
    private TreeResponseDto treeResponseDto;
    private Long prevId;
    private Long nextId;


    @Builder
    public MessageDetailResponseDto(MessageBoxResponseDto responseDto, TreeResponseDto treeResponseDto, Long prevId, Long nextId) {
        this.responseDto = responseDto;
        this.treeResponseDto = treeResponseDto;
        this.prevId = prevId;
        this.nextId = nextId;
    }

    public static MessageDetailResponseDto of(MessageBoxResponseDto responseDto, TreeResponseDto treeResponseDto, Long prevId, Long nextId) {
        return MessageDetailResponseDto.builder()
                .responseDto(responseDto)
                .treeResponseDto(treeResponseDto)
                .prevId(prevId)
                .nextId(nextId)
                .build();
    }
}
