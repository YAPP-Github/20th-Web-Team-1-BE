package com.yapp.betree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.betree.dto.request.MessageRequestDto;
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
import static com.yapp.betree.domain.UserTest.TEST_SAVE_USER;
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
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isLogin", String.valueOf(true))
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
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isLogin", String.valueOf(true))
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C001"))
                .andExpect(jsonPath("$.message").value("Invalid input value"));
    }

    @DisplayName("메세지함 목록 조회")
    @Test
    void getMessageList() throws Exception {

        MessageBoxResponseDto dto1 = MessageBoxResponseDto.of(TEST_SAVE_MESSAGE, TEST_SAVE_USER);
        MessageBoxResponseDto dto2 = MessageBoxResponseDto.of(TEST_SAVE_ANONYMOUS_MESSAGE, TEST_SAVE_USER);
        List<MessageBoxResponseDto> messageDto = Arrays.asList(dto1, dto2);

        given(messageService.getMessageList(anyLong(), any(), anyLong())).willReturn(MessagePageResponseDto.of(messageDto, false));

        mockMvc.perform(get("/api/messages")
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
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                        .param("page", String.valueOf(0))
                        .param("treeId", String.valueOf(19L)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("T001"));

    }

    @DisplayName("열매 맺기")
    @Test
    void updateMessageOpening() throws Exception {

        List<String> idList = Arrays.asList(String.valueOf(9L), String.valueOf(10L));

        mockMvc.perform(put("/api/messages/opening")
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(idList)))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @DisplayName("메세지 삭제")
    @Test
    void deleteMessages() throws Exception {

        List<String> idList = Arrays.asList(String.valueOf(9L), String.valueOf(10L));

        mockMvc.perform(delete("/api/messages")
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(idList)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("메세지 이동")
    @Test
    void moveMessageFolder() throws Exception {

        List<String> idList = Arrays.asList(String.valueOf(9L), String.valueOf(10L));

        mockMvc.perform(put("/api/messages/folder")
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(idList))
                        .param("treeId", String.valueOf(1L)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("메세지 즐겨찾기 상태 변경")
    @Test
    void updateMessageFavorite() throws Exception {

        mockMvc.perform(put("/api/messages/favorite")
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                        .param("messageId", String.valueOf(1L)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("즐겨찾기한 메세지 목록 조회")
    @Test
    void getFavoriteMessageList() throws Exception {

        List<MessageBoxResponseDto> messageDto = Collections.singletonList(MessageBoxResponseDto.of(TEST_SAVE_MESSAGE, TEST_SAVE_USER));

        given(messageService.getFavoriteMessage(anyLong(), any())).willReturn(MessagePageResponseDto.of(messageDto, false));

        mockMvc.perform(get("/api/messages/favorite")
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                        .param("page", String.valueOf(0)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseDto[0].favorite").value(true));
    }
}