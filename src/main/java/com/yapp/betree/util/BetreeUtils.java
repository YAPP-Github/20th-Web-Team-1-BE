package com.yapp.betree.util;

import com.yapp.betree.domain.Message;
import com.yapp.betree.dto.oauth.OAuthUserInfoDto;
import com.yapp.betree.dto.response.MessageResponseDto;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class BetreeUtils {

    private static final Random RANDOM = new Random();
    private static final int RANDOM_BOUND = 4;
    private static final int RANDOM_MIN = 1;
    private static final String BASE_IMAGE_URL = "image/v2/user_";
    private static final String BASE_IMAGE_SUFFIX = ".svg";
    public static final ConcurrentHashMap<Long, String> betreeMessages = new ConcurrentHashMap<Long, String>() {{
        put(-1L, "나무에 물을 주고 나만의 비트리를 길러보세요!");
        put(-2L, "조금만 더 힘 내봅시다!!");
        put(-3L, "여러분 화이팅");
        put(-4L, "넣을만한 좋은 칭찬문구 추천부탁드려요");
        put(-5L, "칭찬메시지 5");
        put(-6L, "칭찬메시지 6");
        put(-7L, "칭찬메시지 7");
        put(-8L, "칭찬메시지 8");
        put(-9L, "칭찬메시지 9");
        put(-10L, "칭찬메시지 10");
        put(-11L, "칭찬메시지 11");
    }};

    public static List<Long> getRandomNum(long size) {
        List<Long> candidate = LongStream.range(1, betreeMessages.size()+1).boxed().collect(Collectors.toList());
        Collections.shuffle(candidate);
        return candidate.subList(0, (int)size);
    }

    // 접속 URL ""로 리턴 -> 프론트에서 알아서 처리
    public static String makeUserAccessUrl(OAuthUserInfoDto user) {
        return "";
    }

    public static String makeUserAccessUrl(Long id) {
        return "";
    }

    public static String makeUserImageNumber() {
        return String.valueOf(RANDOM.nextInt(RANDOM_BOUND) + RANDOM_MIN);
    }

    public static String getImageUrl(String id) {
        if ("0".equals(id)) {
            return BASE_IMAGE_URL + "betree" + BASE_IMAGE_SUFFIX;
        }
        if ("-1".equals(id)) {
            return BASE_IMAGE_URL + "unknown" + BASE_IMAGE_SUFFIX;
        }
        return BASE_IMAGE_URL + id + BASE_IMAGE_SUFFIX;
    }

    public static MessageResponseDto getBetreeMessage(Long id) {
        Long key = id > 0 ? (id * -1) : id;
        String value = betreeMessages.get(key);

        Message message = Message.builder()
                .id(key)
                .senderId(-999L)
                .content(value)
                .anonymous(false)
                .alreadyRead(false)
                .favorite(false)
                .opening(false)
                .build();

        return MessageResponseDto.builder()
                .message(message)
                .senderNickname("Betree")
                .senderProfileImage(getImageUrl("0"))
                .build();
    }
}
