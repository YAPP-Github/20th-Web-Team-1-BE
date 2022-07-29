package com.yapp.betree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.betree.config.TestConfig;
import com.yapp.betree.dto.request.MessageRequestDto;
import com.yapp.betree.dto.request.OpeningRequestDto;
import com.yapp.betree.dto.response.MessageBoxResponseDto;
import com.yapp.betree.dto.response.MessagePageResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.service.JwtTokenTest;
import com.yapp.betree.service.MessageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static com.yapp.betree.domain.MessageTest.TEST_SAVE_ANONYMOUS_MESSAGE;
import static com.yapp.betree.domain.MessageTest.TEST_SAVE_MESSAGE;
import static com.yapp.betree.domain.UserTest.TEST_SAVE_USER_DTO;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("MessageController Test")
@WebMvcTest(MessageController.class)
class MessageControllerTest extends ControllerTest {

    @MockBean
    MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("메세지 생성")
    @Test
    void createMessage() throws Exception {

        Long userId = 1L;
        Long treeId = 1L;

        MessageRequestDto dto = new MessageRequestDto(userId, "메세지를 전송하겠습니다~!", treeId, false);

        given(messageService.createMessage(anyLong(), any())).willReturn(1L);

        mockMvc.perform(post("/api/messages")
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(1L));
    }

    @DisplayName("메세지 생성 - 메세지 글자수 맞지 않을때 예외 발생")
    @Test
    void createMessageCountError() throws Exception {

        Long userId = 1L;
        Long treeId = 1L;

        MessageRequestDto dto = new MessageRequestDto(userId, "10글자 이하", treeId, false);

        given(messageService.createMessage(anyLong(), any())).willReturn(1L);

        mockMvc.perform(post("/api/messages")
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C001"))
                .andExpect(jsonPath("$.message").value("Invalid input value"))
                .andExpect(jsonPath("$.errors[0].message").value("메시지는 최소 10자, 최대 1000자 입력해야합니다."));
    }

    @DisplayName("메세지함 목록 조회")
    @Test
    void getMessageList() throws Exception {

        MessageBoxResponseDto dto1 = MessageBoxResponseDto.of(TEST_SAVE_MESSAGE, TEST_SAVE_USER_DTO);
        MessageBoxResponseDto dto2 = MessageBoxResponseDto.of(TEST_SAVE_ANONYMOUS_MESSAGE, TEST_SAVE_USER_DTO);
        List<MessageBoxResponseDto> messageDto = Arrays.asList(dto1, dto2);

        given(messageService.getMessageList(anyLong(), any(), anyLong())).willReturn(MessagePageResponseDto.of(messageDto, false));

        mockMvc.perform(get("/api/messages")
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                .param("page", String.valueOf(0))
                .param("treeId", String.valueOf(19L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseDto.length()").value(2))
                .andExpect(jsonPath("$.responseDto[0].id").value(TEST_SAVE_MESSAGE.getId()))
                .andExpect(jsonPath("$.responseDto[1].id").value(TEST_SAVE_ANONYMOUS_MESSAGE.getId()));

    }

    @DisplayName("메세지함 목록 조회 - 존재하지 않는 treeId 입력시 예외처리")
    @Test
    void getMessageListErrorTreeId() throws Exception {

        given(messageService.getMessageList(anyLong(), any(), eq(19L))).willThrow(new BetreeException(ErrorCode.TREE_NOT_FOUND));

        mockMvc.perform(get("/api/messages")
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                .param("page", String.valueOf(0))
                .param("treeId", String.valueOf(19L)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("T001"))
                .andExpect(jsonPath("$.message").value("나무가 존재하지 않습니다."));

    }

    @DisplayName("열매 맺기")
    @Test
    void updateMessageOpening() throws Exception {

        List<Long> idList = Arrays.asList(9L, 10L);
        OpeningRequestDto dto = OpeningRequestDto.builder().messageIds(idList).treeId(0L).build();

        mockMvc.perform(put("/api/messages/opening")
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }


    @DisplayName("메세지 삭제")
    @Test
    void deleteMessages() throws Exception {

        List<String> idList = Arrays.asList(String.valueOf(9L), String.valueOf(10L));

        mockMvc.perform(delete("/api/messages")
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idList)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("메세지 이동")
    @Test
    void moveMessageFolder() throws Exception {

        List<String> idList = Arrays.asList(String.valueOf(9L), String.valueOf(10L));

        mockMvc.perform(put("/api/messages/folder")
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idList))
                .param("treeId", String.valueOf(1L)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("메세지 즐겨찾기 상태 변경")
    @Test
    void updateMessageFavorite() throws Exception {

        mockMvc.perform(put("/api/messages/favorite")
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                .param("messageId", String.valueOf(1L)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("즐겨찾기한 메세지 목록 조회")
    @Test
    void getFavoriteMessageList() throws Exception {

        List<MessageBoxResponseDto> messageDto = Collections.singletonList(MessageBoxResponseDto.of(TEST_SAVE_MESSAGE, TEST_SAVE_USER_DTO));

        given(messageService.getFavoriteMessage(anyLong(), any())).willReturn(MessagePageResponseDto.of(messageDto, false));

        mockMvc.perform(get("/api/messages/favorite")
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                .param("page", String.valueOf(0)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseDto[0].favorite").value(true));
    }

    @DisplayName("메세지 읽음 상태 변경")
    @Test
    void updateMessageRead() throws Exception {

        mockMvc.perform(put("/api/messages/alreadyRead")
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                .param("messageId", String.valueOf(1L)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("메세지 상세 조회 - 존재하지 않는 messageId 예외처리")
    @Test
    void getMessageDetail() throws Exception {

        given(messageService.getMessageDetail(anyLong(), eq(1L), eq(false))).willThrow(new BetreeException(ErrorCode.MESSAGE_NOT_FOUND));

        mockMvc.perform(get("/api/messages/1")
                        .cookie(TestConfig.COOKIE_TOKEN)
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("M001"))
                .andExpect(jsonPath("$.message").value("메세지가 존재하지 않습니다."));
    }
}