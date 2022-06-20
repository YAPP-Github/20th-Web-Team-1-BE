package com.yapp.betree.controller;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.UserInfoFixture;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.UserRepository;
import com.yapp.betree.service.oauth.KakaoApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
public class AcceptanceTest {

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FolderRepository folderRepository;

    @MockBean
    private KakaoApiService kakaoApiService;

    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("회원가입 테스트 - 유저 생성, 폴더 생성")
    void signUpTest() throws Exception {
        given(kakaoApiService.getOauthId("accessToken")).willReturn(1L);
        given(kakaoApiService.getUserInfo("accessToken")).willReturn(UserInfoFixture.createOAuthUserInfo());

        mockMvc.perform(get("/api/signin")
                .header("X-Kakao-Access-Token", "accessToken"))
                .andDo(print())
                .andExpect(status().isCreated());

        List<User> users = userRepository.findAll();
        assertThat(users.get(0).getNickName()).isEqualTo("user");

        List<Folder> folders = folderRepository.findAll();
        assertThat(folders.get(0).getFruit()).isEqualTo(FruitType.DEFAULT);

        assertThat(users.get(0).getFolders().get(0).getId()).isEqualTo(folders.get(0).getId());
    }
}
