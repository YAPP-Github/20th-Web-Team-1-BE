package com.yapp.betree.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeResponseDto {

    private int totalUnreadMessageCount;
    private List<MessageResponseDto> messages;

    @Builder
    public NoticeResponseDto(int totalUnreadMessageCount, List<MessageResponseDto> messages) {
        this.totalUnreadMessageCount = totalUnreadMessageCount;
        this.messages = messages;
    }
}
