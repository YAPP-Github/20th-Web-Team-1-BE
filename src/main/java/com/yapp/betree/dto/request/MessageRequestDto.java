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
    private String folderName;
    private boolean anonymous;

    @Builder
    public MessageRequestDto(Long receiverId, String content, String folderName, boolean anonymous) {
        this.receiverId = receiverId;
        this.content = content;
        this.folderName = folderName;
        this.anonymous = anonymous;
    }
}
