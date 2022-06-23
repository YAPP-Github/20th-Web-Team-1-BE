package com.yapp.betree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.dto.response.TreeResponseDto;
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
import static org.mockito.BDDMockito.given;
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

    @DisplayName("유저 나무숲 조회 - 존재하지 않는 userId는 예외가 발생한다.")
    @Test
    void userFailForest() throws Exception {
        Long userId = 1L;
        given(userService.isExist(userId)).willReturn(false);

        mockMvc.perform(get("/api/forest")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value(ErrorCode.USER_NOT_FOUND.getCode()))
                .andDo(print());
    }

    @DisplayName("유저 나무숲 조회 - userId에 해당하는 유저가 가진 나무숲이 전체 조회된다.")
    @Test
    void userForest() throws Exception {
        Long userId = 1L;
        given(userService.isExist(userId)).willReturn(true);
        given(folderService.userForest(userId)).willReturn(Lists.newArrayList(TreeResponseDto.of(TEST_SAVE_DEFAULT_TREE)));

        mockMvc.perform(get("/api/forest")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_SAVE_DEFAULT_TREE.getId()))
                .andDo(print());
    }

    @DisplayName("유저 상세 나무 조회")
    @Test
    void userDetailTree() throws Exception {

        mockMvc.perform(get("/api/forest/10")
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", String.valueOf(1L)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("나무 추가")
    @Test
    void createTree() throws Exception {

        Map<String, Object> input = new HashMap<>();

        input.put("name", "new folder");
        input.put("fruitType", FruitType.APPLE);

        mockMvc.perform(post("/api/forest")
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", String.valueOf(1L))
                .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("나무 편집")
    @Test
    void updateTree() throws Exception {

        Map<String, Object> input = new HashMap<>();

        input.put("name", "update folder");
        input.put("fruitType", FruitType.APPLE);

        mockMvc.perform(put("/api/forest/18")
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", String.valueOf(1L))
                .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}
