package com.yapp.betree.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageRequestDto {

    private Long receiverId;
    private String content;
    private Long folderId;
    private boolean anonymous;

    @Builder
    public MessageRequestDto(Long receiverId, String content, Long folderId, boolean anonymous) {
        this.receiverId = receiverId;
        this.content = content;
        this.folderId = folderId;
        this.anonymous = anonymous;
    }
}
