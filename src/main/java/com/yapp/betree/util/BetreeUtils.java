package com.yapp.betree.util;

import com.yapp.betree.domain.Message;
import com.yapp.betree.dto.oauth.OAuthUserInfoDto;
import com.yapp.betree.dto.response.MessageResponseDto;

import java.util.concurrent.ConcurrentHashMap;

public class BetreeUtils {

    public static final ConcurrentHashMap<Long, String> betreeMessages = new ConcurrentHashMap<Long, String>() {{
        put(-1L, "칭찬메시지1");
        put(-2L, "칭찬메시지2");
        put(-3L, "칭찬메시지3");
        put(-4L, "칭찬메시지4");
        put(-5L, "칭찬메시지5");
        put(-6L, "칭찬메시지6");
        put(-7L, "칭찬메시지7");
        put(-8L, "칭찬메시지8");

    }};

    // TODO 유저 접속 URL 생성 필요
    public static String makeUserAccessUrl(OAuthUserInfoDto user) {
        return "accessUrl";
    }

    public static String makeUserAccessUrl(Long oauthId) {
        return "accessUrl";
    }

    public static MessageResponseDto getBetreeMessage(Long id) {
        Long key = id * -1;
        String value = betreeMessages.get(key);

        Message message = Message.builder()
                .id(key)
                .content(value)
                .anonymous(false)
                .alreadyRead(false)
                .favorite(false)
                .opening(false)
                .build();

        return MessageResponseDto.builder()
                .message(message)
                .senderNickname("Betree")
                .senderProfileImage("Betree 이미지")
                .build();
    }
}
