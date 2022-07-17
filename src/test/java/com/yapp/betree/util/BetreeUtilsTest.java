package com.yapp.betree.util;

import com.yapp.betree.dto.response.MessageResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("유틸 클래스 테스트")
public class BetreeUtilsTest {

    @Test
    @DisplayName("기본 칭찬메시지 테스트")
    void betreeMessageTest() {
        MessageResponseDto betreeMessage = BetreeUtils.getBetreeMessage(1L);
        System.out.println(betreeMessage);
    }

    @Test
    @DisplayName("1~4 랜덤숫자 생성 테스트")
    void makeUesrImageTest() {
        String randomNumber = BetreeUtils.makeUserImageNumber();
        assertThat(randomNumber).isGreaterThanOrEqualTo("1");
        assertThat(randomNumber).isLessThanOrEqualTo("4");
    }
}
