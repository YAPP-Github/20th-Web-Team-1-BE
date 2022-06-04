package com.yapp.betree.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponseDto {

    private Long id;
    private String content;
    private boolean anonymous;
    private String senderNickName;
    private String senderProfileImage;

    @Builder
    public MessageResponseDto(Long id, String content, boolean anonymous, String senderNickName, String senderProfileImage) {
        this.id = id;
        this.content = content;
        this.anonymous = anonymous;
        this.senderNickName = senderNickName;
        this.senderProfileImage = senderProfileImage;
    }
}
