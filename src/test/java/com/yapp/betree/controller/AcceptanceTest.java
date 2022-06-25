package com.yapp.betree.controller;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.UserInfoFixture;
import com.yapp.betree.dto.response.MessageResponseDto;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.UserRepository;
import com.yapp.betree.service.oauth.KakaoApiService;
import com.yapp.betree.util.BetreeUtils;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    private MessageRepository messageRepository;

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
        assertThat(users.get(0).getNickname()).isEqualTo("닉네임");

        List<Folder> folders = folderRepository.findAll();
        assertThat(folders.get(0).getFruit()).isEqualTo(FruitType.DEFAULT);

        assertThat(users.get(0).getFolders().get(0).getId()).isEqualTo(folders.get(0).getId());
    }

    @Test
    @DisplayName("안읽은 메시지 조회 테스트")
    void findByUserIdAndAlreadyReadTest() {
        Folder folder = Folder.builder()
                .name("폴더")
                .fruit(FruitType.APPLE)
                .level(0L)
                .build();

        User user = User.builder()
                .nickname("닉네임")
                .email("이메일")
                .oauthId(1L)
                .userImage("이미지")
                .url("url")
                .lastAccessTime(LocalDateTime.now())
                .build();
        user.addFolder(folder);

        userRepository.save(user);

        Message message = Message.builder()
                .content("안녕")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(false)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();

        messageRepository.save(message);

        Message message1 = Message.builder()
                .content("안녕")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(true)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();
        messageRepository.save(message1);

        // 안읽은 메시지
        List<Message> unreadMessages = messageRepository.findByUserIdAndAlreadyRead(user.getId(), false);
        assertThat(unreadMessages).hasSize(1);

        // 안읽은 메시지 먼저 8개 리스트에 넣음
        List<MessageResponseDto> noticeTreeMessages = new ArrayList<>();
        for (Message m : unreadMessages) {
            User sender = userRepository.findById(m.getSenderId()).get();
            noticeTreeMessages.add(MessageResponseDto.of(m, sender));
        }

        // 즐겨찾기 메시지 (일단 생략)

        // 비트리 제공 메시지로 8개까지 다시 채움
        long remainCount = 8 - noticeTreeMessages.size();
        for (long i = 1; i <= remainCount; i++) {
            noticeTreeMessages.add(BetreeUtils.getBetreeMessage(i));
        }

        assertThat(noticeTreeMessages).hasSize(8);
        System.out.println(noticeTreeMessages);
    }
}
