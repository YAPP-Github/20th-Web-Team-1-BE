package com.yapp.betree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.betree.config.TestConfig;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.MessageTest;
import com.yapp.betree.dto.SendUserDto;
import com.yapp.betree.dto.response.ForestResponseDto;
import com.yapp.betree.dto.response.MessageResponseDto;
import com.yapp.betree.dto.response.TreeFullResponseDto;
import com.yapp.betree.dto.response.TreeResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.service.FolderService;
import com.yapp.betree.service.JwtTokenTest;
import com.yapp.betree.service.UserService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static com.yapp.betree.domain.FolderTest.TEST_SAVE_DEFAULT_TREE;
import static com.yapp.betree.domain.UserTest.TEST_SAVE_USER;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("ForestController Test")
@WebMvcTest(ForestController.class)
public class ForestControllerTest extends ControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private FolderService folderService;

    @DisplayName("유저 나무숲 , 상세 나무 조회 - 존재하지 않는 userId는 예외가 발생한다.")
    @Test
    void userFailForestTest() throws Exception {
        Long userId = 1L;
        given(userService.isExist(userId)).willReturn(false);

        mockMvc.perform(get("/api/forest")
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value(ErrorCode.USER_NOT_FOUND.getCode()))
                .andDo(print());

        mockMvc.perform(get("/api/forest/1")
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value(ErrorCode.USER_NOT_FOUND.getCode()))
                .andDo(print());
    }

    @DisplayName("유저 나무숲 조회 - 본인 나무숲은 전체 조회된다.")
    @Test
    void userForestTest() throws Exception {
        Long userId = 1L;

        ForestResponseDto dto = ForestResponseDto.builder()
                .nickname(TEST_SAVE_USER.getNickname())
                .responseDtoList(Lists.newArrayList(TreeResponseDto.of(TEST_SAVE_DEFAULT_TREE))).build();

        given(userService.isExist(userId)).willReturn(true);
        given(folderService.userForest(userId, userId)).willReturn(dto);

        mockMvc.perform(get("/api/forest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(TestConfig.COOKIE_TOKEN)
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseDtoList[0].id").value(TEST_SAVE_DEFAULT_TREE.getId()))
                .andDo(print());
    }

    @DisplayName("유저 나무숲 조회 - 본인이 아닌 유저의 나무숲은 공개 나무만 조회된다.")
    @Test
    void otherUserForestTest() throws Exception {
        Long userId = 2L;
        given(userService.isExist(userId)).willReturn(true);
        given(folderService.userForest(userId, userId)).willReturn(any());

        mockMvc.perform(get("/api/forest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(TestConfig.COOKIE_TOKEN)
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andDo(print());
    }

    @DisplayName("유저 상세 나무 조회")
    @Test
    void userDetailTreeTest() throws Exception {
        Long userId = 1L;
        given(userService.isExist(userId)).willReturn(true);
        given(folderService.userDetailTree(userId, 1L, userId)).willReturn(
                TreeFullResponseDto.builder()
                        .folder(TEST_SAVE_DEFAULT_TREE)
                        .messages(Lists.newArrayList(MessageResponseDto.of(MessageTest.TEST_SAVE_MESSAGE, SendUserDto.of(TEST_SAVE_USER))))
                        .prevId(0L)
                        .nextId(0L)
                        .build()
        );
        mockMvc.perform(get("/api/forest/1")
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", String.valueOf(userId)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("나무 추가")
    @Test
    void createTreeTest() throws Exception {
        // given
        Map<String, Object> input = new HashMap<>();
        input.put("name", "추가폴더이름");
        input.put("fruitType", FruitType.APPLE);

        // when
        given(folderService.createTree(anyLong(), any())).willReturn(1L);

        mockMvc.perform(post("/api/forest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input))
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(1L))
                .andDo(print());
    }

    @DisplayName("나무 추가 - DEFAULT 나무 추가를 시도하면 예외가 발생한다.")
    @Test
    void createTreeEnumTest() throws Exception {
        // given
        Map<String, Object> input = new HashMap<>();
        input.put("name", "추가폴더이름");
        input.put("fruitType", FruitType.DEFAULT);

        mockMvc.perform(post("/api/forest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input))
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("T002"))
                .andDo(print());
    }

    @DisplayName("나무 추가 - 나무 이름은 10자를 넘을 수 없다.")
    @Test
    void createTreeNameTest() throws Exception {
        // given
        Map<String, Object> input = new HashMap<>();
        input.put("name", "10자 이상 폴더 이름");
        input.put("fruitType", FruitType.DEFAULT);

        mockMvc.perform(post("/api/forest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input))
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C001"))
                .andExpect(jsonPath("$.errors[0].message").value("나무 이름은 10자를 넘을 수 없습니다."))
                .andDo(print());
    }

    @DisplayName("나무 추가 - 나무 이름은 빈값일 수 없다")
    @Test
    void createTreeNameBlankTest() throws Exception {
        // given
        Map<String, Object> input = new HashMap<>();
        input.put("name", "");
        input.put("fruitType", FruitType.DEFAULT);

        mockMvc.perform(post("/api/forest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input))
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C001"))
                .andDo(print());
    }

    @DisplayName("나무 편집")
    @Test
    void updateTreeTest() throws Exception {
        // given
        Map<String, Object> input = new HashMap<>();
        input.put("name", "변경폴더10자이내");
        input.put("fruitType", FruitType.APPLE);

        mockMvc.perform(put("/api/forest/18")
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", String.valueOf(1L))
                .content(objectMapper.writeValueAsString(input))
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(18));
    }

    @DisplayName("나무 편집 - 변경하려는 나무가 DEFAULT타입인 경우 예외가 발생한다.")
    @Test
    void updateTreeDefaultTest() throws Exception {
        // given
        Map<String, Object> input = new HashMap<>();
        input.put("name", "변경폴더이름");
        input.put("fruitType", FruitType.DEFAULT);

        willThrow(new BetreeException(ErrorCode.TREE_DEFAULT_ERROR, "변경할 타입을 기본 나무 이외의 다른 나무로 선택해주세요. treeId = " + 18 + ", FruitType = " + FruitType.DEFAULT))
                .given(folderService).updateTree(anyLong(), anyLong(), any());

        mockMvc.perform(put("/api/forest/18")
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", String.valueOf(1L))
                .content(objectMapper.writeValueAsString(input))
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("T002"));
    }

    @DisplayName("나무 편집 - 나무 이름은 10자를 넘을 수 없다.")
    @Test
    void updateTreeNameTest() throws Exception {
        // given
        Map<String, Object> input = new HashMap<>();
        input.put("name", "수정폴더이름10자이상ㅇㅇ");
        input.put("fruitType", FruitType.DEFAULT);

        mockMvc.perform(put("/api/forest/18")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input))
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C001"))
                .andDo(print());
    }

    @DisplayName("나무 편집 - 나무 이름은 빈값일 수 없다")
    @Test
    void updateTreeNameBlankTest() throws Exception {
        // given
        Map<String, Object> input = new HashMap<>();
        input.put("name", "");
        input.put("fruitType", FruitType.DEFAULT);

        mockMvc.perform(put("/api/forest/18")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input))
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C001"))
                .andDo(print());
    }

    @DisplayName("나무 삭제 - 기본폴더는 삭제할 수 없다")
    @Test
    void deleteTreeTest() throws Exception {
        willThrow(new BetreeException(ErrorCode.TREE_DEFAULT_ERROR))
                .given(folderService).deleteTree(eq(1L), eq(18L));

        mockMvc.perform(delete("/api/forest/18")
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", String.valueOf(1L))
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("T002"));
    }
}
