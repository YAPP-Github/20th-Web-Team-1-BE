package com.yapp.betree.controller;

import com.yapp.betree.config.TestConfig;
import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FolderTest;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.User;
import com.yapp.betree.domain.UserTest;
import com.yapp.betree.dto.UserInfoFixture;
import com.yapp.betree.dto.oauth.JwtTokenDto;
import com.yapp.betree.dto.oauth.OAuthUserInfoDto;
import com.yapp.betree.dto.request.TreeRequestDto;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.UserRepository;
import com.yapp.betree.service.FolderService;
import com.yapp.betree.service.JwtTokenTest;
import com.yapp.betree.service.LoginService;
import com.yapp.betree.service.UserService;
import com.yapp.betree.service.oauth.JwtTokenProvider;
import com.yapp.betree.service.oauth.KakaoApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static com.yapp.betree.config.TestConfig.getClaims;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("?????? ?????? ?????????")
@SpringBootTest
@Transactional
public class FolderAcceptanceTest {

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private FolderService folderService;

    @MockBean
    private LoginService loginService;

    @Autowired
    private FolderRepository folderRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private KakaoApiService kakaoApiService;

    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    public void deleteFolder() throws Exception {

        OAuthUserInfoDto oAuthUserInfo = UserInfoFixture.createOAuthUserInfo();
        User user = userRepository.save(oAuthUserInfo.generateSignUpUser());

        given(kakaoApiService.getOauthId("accessToken")).willReturn(user.getId());
        given(kakaoApiService.getUserInfo("accessToken")).willReturn(oAuthUserInfo);
        given(loginService.createToken("accessToken")).willReturn(JwtTokenDto.builder().build());
        mockMvc.perform(post("/api/signin")
                .header("X-Kakao-Access-Token", "accessToken"))
                .andDo(print())
                .andExpect(status().isCreated());

        folderService.createTree(user.getId(), TreeRequestDto.builder().fruitType(FruitType.APPLE).name("????????????").build());

        Folder apple = folderRepository.findByUserIdAndFruit(user.getId(), FruitType.APPLE);
        Message message = Message.builder()
                .content("?????? ??????")
                .senderId(UserTest.TEST_SAVE_USER.getId())
                .user(user)
                .folder(apple)
                .build();

        messageRepository.save(message);

        String token = JwtTokenTest.JWT_TOKEN_TEST;
        given(jwtTokenProvider.parseToken(token)).willReturn(getClaims(user.getId()));

        // ?????? ??????
        mockMvc.perform(delete("/api/forest/" + apple.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNoContent());

        // ??????, ????????? ?????? ??????
        Optional<Folder> folder = folderRepository.findById(apple.getId());
        assertThat(folder).isEmpty();
        Optional<Message> deleteMessage = messageRepository.findByIdAndUserIdAndDelByReceiver(message.getId(), user.getId(), false);
        assertThat(deleteMessage).isEmpty();
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    @Disabled
    public void openingFolder() throws Exception {
        User user = userRepository.save(UserTest.TEST_USER); // ?????? ??????
        user.addFolder(FolderTest.TEST_APPLE_TREE); // ????????? ??????
        Folder apple = folderRepository.findByUserIdAndFruit(user.getId(), FruitType.APPLE);

        // ???????????? ????????? user ?????? ????????? ?????? ????????? ?????? ??????
        mockMvc.perform(get("/api/forest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(TestConfig.COOKIE_TOKEN)
                        .param("userId", String.valueOf(user.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
        ;

        String token = JwtTokenTest.JWT_TOKEN_TEST;
        given(jwtTokenProvider.parseToken(token)).willReturn(getClaims(user.getId()));

        // ?????? ?????? ????????? ?????? ????????? ?????? 1???
        mockMvc.perform(get("/api/forest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(TestConfig.COOKIE_TOKEN)
                        .param("userId", String.valueOf(user.getId()))
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(apple.getId()))
        ;

        // ?????? ????????? ??????
        mockMvc.perform(put("/api/forest/opening")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(TestConfig.COOKIE_TOKEN)
                        .param("treeId", String.valueOf(apple.getId()))
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNoContent())
        ;

        // ???????????? ????????? user ?????? ????????? ?????? ????????? ?????? 1???
        mockMvc.perform(get("/api/forest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(TestConfig.COOKIE_TOKEN)
                        .param("userId", String.valueOf(user.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(apple.getId()))
        ;
    }
}
