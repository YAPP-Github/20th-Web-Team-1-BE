package com.yapp.betree.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageDetailResponseDto {

    private MessageBoxResponseDto responseDto;
    private Long prevId;
    private Long nextId;


    @Builder
    public MessageDetailResponseDto(MessageBoxResponseDto responseDto, Long prevId, Long nextId) {
        this.responseDto = responseDto;
        this.prevId = prevId;
        this.nextId = nextId;
    }

    public static MessageDetailResponseDto of(MessageBoxResponseDto responseDto, Long prevId, Long nextId) {
        return MessageDetailResponseDto.builder()
                .responseDto(responseDto)
                .prevId(prevId)
                .nextId(nextId)
                .build();
    }
}
