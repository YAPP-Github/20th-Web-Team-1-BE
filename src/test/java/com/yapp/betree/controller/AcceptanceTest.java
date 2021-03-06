package com.yapp.betree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.betree.config.TestConfig;
import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FolderTest;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.User;
import com.yapp.betree.domain.UserTest;
import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.dto.SendUserDto;
import com.yapp.betree.dto.UserInfoFixture;
import com.yapp.betree.dto.oauth.OAuthUserInfoDto;
import com.yapp.betree.dto.response.MessageResponseDto;
import com.yapp.betree.dto.response.NoticeResponseDto;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.UserRepository;
import com.yapp.betree.service.MessageService;
import com.yapp.betree.service.JwtTokenTest;
import com.yapp.betree.service.UserService;
import com.yapp.betree.service.oauth.JwtTokenProvider;
import com.yapp.betree.service.oauth.KakaoApiService;
import com.yapp.betree.util.BetreeUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.LocalDateTime;
import java.util.*;

import static com.yapp.betree.domain.UserTest.TEST_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
public class AcceptanceTest {
    @Autowired
    WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KakaoApiService kakaoApiService;

    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("???????????? ????????? - ?????? ??????, ?????? ??????")
    void signUpTest() throws Exception {
        OAuthUserInfoDto oAuthUserInfo = UserInfoFixture.createOAuthUserInfo();
        User save = userRepository.save(oAuthUserInfo.generateSignUpUser());

        given(kakaoApiService.getOauthId("accessToken")).willReturn(save.getId());
        given(kakaoApiService.getUserInfo("accessToken")).willReturn(oAuthUserInfo);

        mockMvc.perform(post("/api/signin")
                .header("X-Kakao-Access-Token", "accessToken"))
                .andDo(print())
                .andExpect(status().isCreated());

        User user = userRepository.findById(save.getId()).get();
        assertThat(user.getNickname()).isEqualTo("?????????");

        List<Folder> folders = folderRepository.findAllByUserId(save.getId());
        assertThat(folders.get(0).getFruit()).isEqualTo(FruitType.DEFAULT);

        assertThat(user.getFolders().get(0).getId()).isEqualTo(folders.get(0).getId());

        List<Message> all = messageRepository.findAll();
        Message messages = all.get(all.size()-1);
        assertThat(messages.getSenderId()).isEqualTo(-999L);
        assertThat(messages.getContent()).contains("STEP");
    }

    @Test
    @DisplayName("????????? ????????? ?????? ?????????")
    void findByUserIdAndAlreadyReadAndDelByReceiverTest() {

        TEST_USER.addFolder(FolderTest.TEST_APPLE_TREE);

        User user = userRepository.save(TEST_USER);

        Message message = Message.builder()
                .content("???????????????0-??????,?????????")
                .senderId(user.getId())
                .anonymous(true)
                .alreadyRead(false)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();

        messageRepository.save(message);

        Message message1 = Message.builder()
                .content("???????????????1-????????????,??????")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(true)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();
        messageRepository.save(message1);

        Message message2 = Message.builder()
                .content("???????????????2-????????????,??????,????????????")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(true)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();
        messageRepository.save(message2);
        messageService.updateFavoriteMessage(user.getId(), message2.getId());

        Message message3 = Message.builder()
                .content("???????????????3-????????????,?????????,????????????")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(false)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();
        messageRepository.save(message3);
        messageService.updateFavoriteMessage(user.getId(), message3.getId());

        // ????????? ?????????
        List<Message> unreadMessages = messageRepository.findByUserIdAndAlreadyReadAndDelByReceiver(user.getId(), false, false);
        assertThat(unreadMessages).hasSize(2);

        // ????????? ????????? ?????? 8??? ???????????? ??????
        Set<MessageResponseDto> noticeTreeMessages = new HashSet<>();
        for (Message m : unreadMessages) {
            SendUserDto sender = userService.findBySenderId(message.getSenderId());
            noticeTreeMessages.add(MessageResponseDto.of(m, sender));
        }

        // ???????????? ?????????
        List<Message> favoriteMessages = messageRepository.findAllByUserIdAndFavoriteAndDelByReceiver(user.getId(), true, false);
        for (Message m : favoriteMessages) {
            if (noticeTreeMessages.size() >= 8) break; // 8???????????? ??????
            SendUserDto sender = userService.findBySenderId(m.getSenderId());
            noticeTreeMessages.add(MessageResponseDto.of(m, sender));
        }

        // ????????? ?????? ???????????? 8????????? ?????? ??????
        long remainCount = 8 - noticeTreeMessages.size();
        for (long i = 1; i <= remainCount; i++) {
            noticeTreeMessages.add(BetreeUtils.getBetreeMessage(i));
        }

        assertThat(noticeTreeMessages).hasSize(8);
        System.out.println(noticeTreeMessages);
        /*
        4?????? ????????? 3??? ??????????????? ??????
        [MessageResponseDto(id=-5, content=???????????????5, anonymous=false, senderNickname=Betree, senderProfileImage=Betree ?????????)
        , MessageResponseDto(id=-4, content=???????????????4, anonymous=false, senderNickname=Betree, senderProfileImage=Betree ?????????)
        , MessageResponseDto(id=-3, content=???????????????3, anonymous=false, senderNickname=Betree, senderProfileImage=Betree ?????????)
        , MessageResponseDto(id=-2, content=???????????????2, anonymous=false, senderNickname=Betree, senderProfileImage=Betree ?????????)
        , MessageResponseDto(id=-1, content=???????????????1, anonymous=false, senderNickname=Betree, senderProfileImage=Betree ?????????)
        , MessageResponseDto(id=123, content=???????????????3-????????????,?????????,????????????, anonymous=false, senderNickname=?????????, senderProfileImage=default image uri)
        , MessageResponseDto(id=120, content=???????????????0-??????,?????????, anonymous=true, senderNickname=??????, senderProfileImage="")
        , MessageResponseDto(id=122, content=???????????????2-????????????,??????,????????????, anonymous=false, senderNickname=?????????, senderProfileImage=default image uri)]
         */
    }

    @Disabled
    @Test
    @DisplayName("???????????? ?????? ????????? ?????????")
    void createMessagesNoLoginUserTest() throws Exception {
        Folder folder = FolderTest.TEST_DEFAULT_TREE;
        User user = UserTest.TEST_USER;
        user.addFolder(folder);
        userRepository.save(user);

        Map<String, Object> input = new HashMap<>();

        input.put("receiverId", user.getId());
        input.put("content", "?????????10?????????~~~");
        input.put("anonymous", false);

        MvcResult mvcResult = mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        Long messageId = Long.parseLong(mvcResult.getResponse().getContentAsString());

        Message message = messageRepository.findByIdAndUserIdAndDelByReceiver(messageId, user.getId(), false).get();
        assertThat(message.getSenderId()).isEqualTo(-1L);
        assertThat(message.isAnonymous()).isTrue();

        SendUserDto sender = userService.findBySenderId(message.getSenderId());
        assertThat(sender.getId()).isEqualTo(-1L);
        assertThat(sender.getUserImage()).isEqualTo("1");
        assertThat(sender.getNickname()).isEqualTo("??????");
    }

    @Test
    @DisplayName("???????????? ???????????? ?????????")
    void noticeTreeTest() throws Exception {
        // given
        TEST_USER.addFolder(FolderTest.TEST_APPLE_TREE);
        User user = userRepository.save(TEST_USER);

        Message message = Message.builder()
                .content("???????????????0-??????,?????????")
                .senderId(user.getId())
                .anonymous(true)
                .alreadyRead(false)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();

        messageRepository.save(message);

        Message message1 = Message.builder()
                .content("???????????????1-????????????,????????????")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(false)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();
        messageRepository.save(message1);

        Message message2 = Message.builder()
                .content("???????????????2-????????????,????????????,????????????")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(false)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();
        messageRepository.save(message2);
        messageService.updateFavoriteMessage(user.getId(), message2.getId());

        Message message3 = Message.builder()
                .content("???????????????3-????????????,?????????,????????????")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(false)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();
        messageRepository.save(message3);
        messageService.updateFavoriteMessage(user.getId(), message3.getId());

        // loginUserDto ????????? ????????? Token??? ???????????? ????????????
        LoginUserDto loginUserDto = LoginUserDto.of(user);
        String token = jwtTokenProvider.createAccessToken(loginUserDto);

        // ???????????? ??????
        MvcResult mvcResult = mockMvc.perform(get("/api/notices")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        NoticeResponseDto noticeResponseDto = (NoticeResponseDto) objectMapper.readValue(mvcResult.getResponse().getContentAsString(), NoticeResponseDto.class);
        assertThat(noticeResponseDto.getMessages()).hasSize(8);
        assertThat(noticeResponseDto.getTotalUnreadMessageCount()).isEqualTo(4);

        // ????????? ?????????
        mockMvc.perform(put("/api/messages/alreadyRead")
                .contentType(MediaType.APPLICATION_JSON)
                .param("messageId", String.valueOf(message1.getId()))
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(put("/api/messages/alreadyRead")
                .contentType(MediaType.APPLICATION_JSON)
                .param("messageId", String.valueOf(message2.getId()))
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(put("/api/messages/alreadyRead") // ???????????????
                .contentType(MediaType.APPLICATION_JSON)
                .param("messageId", String.valueOf(-1L))
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        MvcResult mvcResult2 = mockMvc.perform(get("/api/notices")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // ???????????? ?????? -> ???????????? ?????? ?????????
        NoticeResponseDto noticeResponseDto2 = (NoticeResponseDto) objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), NoticeResponseDto.class);
        assertThat(noticeResponseDto2.getMessages()).hasSize(5);
        assertThat(noticeResponseDto2.getTotalUnreadMessageCount()).isEqualTo(2);

        // ??????????????? ??? ??? ??????
        assertThat(
                noticeResponseDto2.getMessages().stream()
                        .filter(messageResponseDto -> messageResponseDto.getId() == message1.getId())
                        .count()
        ).isEqualTo(0);
        assertThat(
                noticeResponseDto2.getMessages().stream()
                        .filter(messageResponseDto -> messageResponseDto.getId() == message2.getId())
                        .count()
        ).isEqualTo(0);
    }

    @Test
    @DisplayName("CORS ????????? - preflight options ????????? ?????? ?????? ??????")
    void corsTest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mockMvc.perform(options("/api/forest"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
