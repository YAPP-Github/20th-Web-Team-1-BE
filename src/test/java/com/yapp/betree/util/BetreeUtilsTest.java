package com.yapp.betree.util;

import com.yapp.betree.dto.response.MessageBoxResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("유틸 클래스 테스트")
public class BetreeUtilsTest {

    @Test
    @DisplayName("기본 칭찬메시지 테스트")
    void betreeMessageTest() {
        MessageBoxResponseDto betreeMessage = BetreeUtils.getBetreeMessage(1L);
        System.out.println(betreeMessage);
    }
}
