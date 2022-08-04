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
    private static final String BASE_IMAGE_SUFFIX = ".png";
    public static final ConcurrentHashMap<Long, String> betreeMessages = new ConcurrentHashMap<Long, String>() {{
        put(-1L, "오늘 하루도 수고했다!! 내일도 잘 살아보자~ ");
        put(-2L, "지금 잘 하고 있어 ! 오늘은 훨씬 더 잘했어!");
        put(-3L, "자기가 행복해지는 순간을 잘 알고 그 시간을 만들기 위해 애쓰는 사람으로 크는 일, 나는 이보다 더 중요한 게 없다고 생각한다. -태도의말들 중-");
        put(-4L, "Hike Own Your Hike!");
        put(-5L, "무럭무럭 자라서 거대한 숲을 만들어내길!");
        put(-6L, "그 때 최선을 다했으면 충분하다.\n" +
                "다음에 그것보다 하나 더 해내면 된다 !");
        put(-7L, "+\n" +
                "　 　 n　　　　　 +\n" +
                "　　 (　} ∧＿∧\n" +
                "+　　＼(・ω・ ) 네가\n" +
                "　　　 　| 　　ヽ 최고야!\n" +
                "　　 　 /　＿とノ\n" +
                "　　　 ,ゝ,,,)　 ヽ,,)");
        put(-8L, "네가 어떤 삶을 살든 나는 너를 응원할 것이다.");
        put(-9L, "나는 내가 마음껏 행복했으면 좋겠다.\n" +
                "사소한 것에도 많이 웃고\n" +
                "작은 일에 많이 아파하지 않고\n" +
                "단단한 사람으로\n" +
                "모든일에 담담해졌으면 좋겠다.");
        put(-10L, "오늘 하루 어땠어? 별일 없었어?");
        put(-11L, "알아요 알아. 내가 최고라는거.");
        put(-12L, "나는 네가 너라서 좋아.");
        put(-14L, "처음부터 겁먹지 말자. 막상 가보면 아무것도 아닌 게 세상에는 참 많다.");
        put(-15L, "행복의 한 쪽 문이 닫힐 때, 다른 한 쪽 문은 열린다.\n" +
                "하지만 우리는 그 닫힌 문만 오래 바라보느라\n" +
                "우리에게 열린 다른 문은 못 보곤 한다.");
        put(-16L, "너 지금 멋지게 헤엄치려고 숨 참는 것부터 하고 있다고 생각해");
        put(-17L, "수고했어 오늘도");
        put(-18L, "＼＼(۶•̀ᴗ•́)۶//／／");
        put(-19L, "d=(´▽｀)=b");
        put(-20L, "✧｡٩(ˊᗜˋ)و✧*｡");
        put(-21L, "（´•̥  ̫ •̥`⑅)つ♥");
        put(-22L, "٩(๑•̀o•́๑)و");
        put(-23L, "(ᴗ̤ .̮ ᴗ̤  )₎₎ᵗᑋᵃᐢᵏ ᵞᵒᵘෆو");
    }};

    public static List<Long> getRandomNum(long size) {
        List<Long> candidate = LongStream.range(1, betreeMessages.size() + 1).boxed().collect(Collectors.toList());
        Collections.shuffle(candidate);
        return candidate.subList(0, (int) size);
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
