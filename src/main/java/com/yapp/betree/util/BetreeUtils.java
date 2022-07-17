package com.yapp.betree.util;

import com.yapp.betree.domain.Message;
import com.yapp.betree.dto.oauth.OAuthUserInfoDto;
import com.yapp.betree.dto.response.MessageResponseDto;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class BetreeUtils {

    private static final Random RANDOM = new Random();
    private static final int RANDOM_BOUND = 4;
    private static final int RANDOM_MIN = 1;
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

    // 접속 URL ""로 리턴 -> 프론트에서 알아서 처리
    public static String makeUserAccessUrl(OAuthUserInfoDto user) {
        return "";
    }

    public static String makeUserAccessUrl(Long id) {
        return "";
    }

    public static String makeUserImage() {
        return String.valueOf(RANDOM.nextInt(RANDOM_BOUND) + RANDOM_MIN);
    }

    public static MessageResponseDto getBetreeMessage(Long id) {
        Long key = id > 0 ? (id * -1) : id;
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
                .senderProfileImage("1")
                .build();
    }
}
