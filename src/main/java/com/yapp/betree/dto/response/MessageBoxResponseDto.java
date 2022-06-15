package com.yapp.betree.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yapp.betree.domain.Message;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageBoxResponseDto {

    private Long id;
    private String content;
    private boolean anonymous;
    private boolean alreadyRead;
    private boolean favorite;
    private boolean opening;
    private String senderNickName;
    private String senderProfileImage;

    @Builder
    public MessageBoxResponseDto(Message message, String senderNickName, String senderProfileImage) {
        this.id = message.getId();
        this.content = message.getContent();
        this.anonymous = message.isAnonymous();
        this.alreadyRead = message.isAlreadyRead();
        this.favorite = message.isFavorite();
        this.opening = message.isOpening();
        this.senderNickName = senderNickName;
        this.senderProfileImage = senderProfileImage;
    }
}
