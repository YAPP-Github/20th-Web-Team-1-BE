package com.yapp.betree.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessagePageResponseDto {

    private List<MessageBoxResponseDto> responseDto;
    private boolean hasNext;

    @Builder
    public MessagePageResponseDto(List<MessageBoxResponseDto> responseDto, boolean hasNext) {
        this.responseDto = responseDto;
        this.hasNext = hasNext;
    }

    public static MessagePageResponseDto of(List<MessageBoxResponseDto> responseDto, boolean hasNext) {
        return MessagePageResponseDto.builder()
                .responseDto(responseDto)
                .hasNext(hasNext)
                .build();
    }
}
