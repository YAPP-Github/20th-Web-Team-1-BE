package com.yapp.betree.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yapp.betree.domain.Message;
import com.yapp.betree.dto.SendUserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@ToString
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

    @Override
    public int hashCode() {
        return Objects.hash(id, content, anonymous, senderNickname, senderProfileImage);
    }

    @Override
    public boolean equals(Object obj) {
        MessageResponseDto objDto = (MessageResponseDto) obj;
        if (this.id != objDto.getId()) {
            return false;
        }
        if (!this.content.equals(objDto.getContent())) {
            return false;
        }
        if (this.anonymous != objDto.isAnonymous()) {
            return false;
        }
        if (!this.senderNickname.equals(objDto.getSenderNickname())) {
            return false;
        }
        if (!this.senderProfileImage.equals(objDto.getSenderProfileImage())) {
            return false;
        }
        return true;
    }
}