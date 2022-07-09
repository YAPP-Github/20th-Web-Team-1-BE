package com.yapp.betree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.betree.dto.response.UserResponseDto;
import com.yapp.betree.service.JwtTokenTest;
import com.yapp.betree.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.yapp.betree.domain.UserTest.TEST_SAVE_USER;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("User 컨트롤러 테스트")
@WebMvcTest(UserController.class)
public class UserControllerTest extends ControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("유저 정보 조회")
    @Test
    void getUserInfo() throws Exception {

        given(userService.getUser(TEST_SAVE_USER.getId())).willReturn(UserResponseDto.of(TEST_SAVE_USER));

        mockMvc.perform(get("/api/users/info")
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @DisplayName("유저 닉네임 변경")
    @Test
    void updateUserNickname() throws Exception {

        mockMvc.perform(put("/api/users/nickname")
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                        .param("nickname", "변경 닉네임"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(TEST_SAVE_USER.getId()));
    }

    @DisplayName("유저 닉네임 변경 - 공백일때 예외처리")
    @Test
    void updateUserNicknameBlank() throws Exception {

        mockMvc.perform(put("/api/users/nickname")
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                        .param("nickname", ""))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[*].message").value("닉네임은 빈값일 수 없습니다."));
    }

    @DisplayName("유저 닉네임 변경 - 글자수 20자 초과시 예외처리")
    @Test
    void updateUserNicknameCount() throws Exception {

        mockMvc.perform(put("/api/users/nickname")
                        .header("Authorization", "Bearer " + JwtTokenTest.JWT_TOKEN_TEST)
                        .param("nickname", "일이삼사오육칠팔구십일이삼사오육칠팔구십일"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[*].message").value("닉네임은 20자를 넘을 수 없습니다."));
    }
}
