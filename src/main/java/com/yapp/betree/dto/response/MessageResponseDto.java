package com.yapp.betree.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.SendUserDto;
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
    private String senderNickname;
    private String senderProfileImage;

    @Builder
    public MessageResponseDto(Message message, String senderNickname, String senderProfileImage) {
        this.id = message.getId();
        this.content = message.getContent();
        this.anonymous = message.isAnonymous();
        this.senderNickname = senderNickname;
        this.senderProfileImage = senderProfileImage;
    }

    public static MessageResponseDto of(Message message, SendUserDto user) {
        return MessageResponseDto.builder()
                .message(message)
                .senderNickname(message.isAnonymous() ? "익명" : user.getNickname())
                .senderProfileImage(message.isAnonymous() ? "기본 이미지" : user.getUserImage())
                .build();
    }
}