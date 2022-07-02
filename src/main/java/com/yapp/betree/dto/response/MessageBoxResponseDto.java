package com.yapp.betree.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yapp.betree.domain.Message;
import com.yapp.betree.dto.SendUserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageBoxResponseDto {

    private Long id;
    private String content;
    private boolean anonymous;
    private boolean alreadyRead;
    private boolean favorite;
    private boolean opening;
    private String senderNickname;
    private String senderProfileImage;
    private LocalDateTime createdDate;

    @Builder
    public MessageBoxResponseDto(Message message, String senderNickname, String senderProfileImage) {
        this.id = message.getId();
        this.content = message.getContent();
        this.anonymous = message.isAnonymous();
        this.alreadyRead = message.isAlreadyRead();
        this.favorite = message.isFavorite();
        this.opening = message.isOpening();
        this.senderNickname = senderNickname;
        this.senderProfileImage = senderProfileImage;
        this.createdDate= message.getCreatedDate();
    }

    public static MessageBoxResponseDto of(Message message, SendUserDto user) {
        return MessageBoxResponseDto.builder()
                .message(message)
                .senderNickname(message.isAnonymous() ? "익명" : user.getNickname())
                .senderProfileImage(message.isAnonymous() ? "기본 이미지" : user.getUserImage())
                .build();
    }
}
