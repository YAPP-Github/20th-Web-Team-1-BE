package com.yapp.betree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    @DisplayName("회원가입 테스트 - 유저 생성, 폴더 생성")
    void signUpTest() throws Exception {
        OAuthUserInfoDto oAuthUserInfo = UserInfoFixture.createOAuthUserInfo();
        User save = userRepository.save(oAuthUserInfo.generateSignUpUser());

        given(kakaoApiService.getOauthId("accessToken")).willReturn(save.getId());
        given(kakaoApiService.getUserInfo("accessToken")).willReturn(oAuthUserInfo);

        mockMvc.perform(get("/api/signin")
                .header("X-Kakao-Access-Token", "accessToken"))
                .andDo(print())
                .andExpect(status().isCreated());

        User user = userRepository.findById(save.getId()).get();
        assertThat(user.getNickname()).isEqualTo("닉네임");

        List<Folder> folders = folderRepository.findAllByUserId(save.getId());
        assertThat(folders.get(0).getFruit()).isEqualTo(FruitType.DEFAULT);

        assertThat(user.getFolders().get(0).getId()).isEqualTo(folders.get(0).getId());
    }

    @Test
    @DisplayName("안읽은 메시지 조회 테스트")
    void findByUserIdAndAlreadyReadTest() {

        TEST_USER.addFolder(FolderTest.TEST_APPLE_TREE);

        User user = userRepository.save(TEST_USER);

        Message message = Message.builder()
                .content("보낸메시지0-익명,안읽음")
                .senderId(user.getId())
                .anonymous(true)
                .alreadyRead(false)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();

        messageRepository.save(message);

        Message message1 = Message.builder()
                .content("보낸메시지1-익명아님,읽음")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(true)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();
        messageRepository.save(message1);

        Message message2 = Message.builder()
                .content("보낸메시지2-익명아님,읽음,즐겨찾기")
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
                .content("보낸메시지3-익명아님,안읽음,즐겨찾기")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(false)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();
        messageRepository.save(message3);
        messageService.updateFavoriteMessage(user.getId(), message3.getId());

        // 안읽은 메시지
        List<Message> unreadMessages = messageRepository.findByUserIdAndAlreadyRead(user.getId(), false);
        assertThat(unreadMessages).hasSize(2);

        // 안읽은 메시지 먼저 8개 리스트에 넣음
        Set<MessageResponseDto> noticeTreeMessages = new HashSet<>();
        for (Message m : unreadMessages) {
            SendUserDto sender = userService.findBySenderId(message.getSenderId());
            noticeTreeMessages.add(MessageResponseDto.of(m, sender));
        }

        // 즐겨찾기 메시지
        List<Message> favoriteMessages = messageRepository.findAllByUserIdAndFavorite(user.getId(), true);
        for (Message m : favoriteMessages) {
            if (noticeTreeMessages.size() >= 8) break; // 8개까지만 담음
            SendUserDto sender = userService.findBySenderId(m.getSenderId());
            noticeTreeMessages.add(MessageResponseDto.of(m, sender));
        }

        // 비트리 제공 메시지로 8개까지 다시 채움
        long remainCount = 8 - noticeTreeMessages.size();
        for (long i = 1; i <= remainCount; i++) {
            noticeTreeMessages.add(BetreeUtils.getBetreeMessage(i));
        }

        assertThat(noticeTreeMessages).hasSize(8);
        System.out.println(noticeTreeMessages);
        /*
        4개가 아니라 3개 들어가는거 확인
        [MessageResponseDto(id=-5, content=칭찬메시지5, anonymous=false, senderNickname=Betree, senderProfileImage=Betree 이미지)
        , MessageResponseDto(id=-4, content=칭찬메시지4, anonymous=false, senderNickname=Betree, senderProfileImage=Betree 이미지)
        , MessageResponseDto(id=-3, content=칭찬메시지3, anonymous=false, senderNickname=Betree, senderProfileImage=Betree 이미지)
        , MessageResponseDto(id=-2, content=칭찬메시지2, anonymous=false, senderNickname=Betree, senderProfileImage=Betree 이미지)
        , MessageResponseDto(id=-1, content=칭찬메시지1, anonymous=false, senderNickname=Betree, senderProfileImage=Betree 이미지)
        , MessageResponseDto(id=123, content=보낸메시지3-익명아님,안읽음,즐겨찾기, anonymous=false, senderNickname=닉네임, senderProfileImage=default image uri)
        , MessageResponseDto(id=120, content=보낸메시지0-익명,안읽음, anonymous=true, senderNickname=익명, senderProfileImage=기본 이미지)
        , MessageResponseDto(id=122, content=보낸메시지2-익명아님,읽음,즐겨찾기, anonymous=false, senderNickname=닉네임, senderProfileImage=default image uri)]
         */
    }

    @Disabled
    @Test
    @DisplayName("비로그인 유저 물주기 테스트")
    void createMessagesNoLoginUserTest() throws Exception {
        Folder folder = FolderTest.TEST_DEFAULT_TREE;
        User user = UserTest.TEST_USER;
        user.addFolder(folder);
        userRepository.save(user);

        Map<String, Object> input = new HashMap<>();

        input.put("receiverId", user.getId());
        input.put("content", "메시지10자이상~~~");
        input.put("anonymous", false);

        MvcResult mvcResult = mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        Long messageId = Long.parseLong(mvcResult.getResponse().getContentAsString());

        Message message = messageRepository.findByIdAndUserId(messageId, user.getId()).get();
        assertThat(message.getSenderId()).isEqualTo(-1L);
        assertThat(message.isAnonymous()).isTrue();

        SendUserDto sender = userService.findBySenderId(message.getSenderId());
        assertThat(sender.getId()).isEqualTo(-1L);
        assertThat(sender.getUserImage()).isEqualTo("기본이미지");
        assertThat(sender.getNickname()).isEqualTo("익명");
    }

    @Test
    @DisplayName("알림나무 읽음처리 테스트")
    void noticeTreeTest() throws Exception {
        // given
        TEST_USER.addFolder(FolderTest.TEST_APPLE_TREE);
        User user = userRepository.save(TEST_USER);

        Message message = Message.builder()
                .content("보낸메시지0-익명,안읽음")
                .senderId(user.getId())
                .anonymous(true)
                .alreadyRead(false)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();

        messageRepository.save(message);

        Message message1 = Message.builder()
                .content("보낸메시지1-익명아님,읽을예정")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(false)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();
        messageRepository.save(message1);

        Message message2 = Message.builder()
                .content("보낸메시지2-익명아님,읽을예정,즐겨찾기")
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
                .content("보낸메시지3-익명아님,안읽음,즐겨찾기")
                .senderId(user.getId())
                .anonymous(false)
                .alreadyRead(false)
                .favorite(false)
                .opening(false)
                .user(user)
                .build();
        messageRepository.save(message3);
        messageService.updateFavoriteMessage(user.getId(), message3.getId());

        // loginUserDto 임시로 만들어 Token도 생성해서 요청보냄
        LoginUserDto loginUserDto = LoginUserDto.of(user);
        String token = jwtTokenProvider.createAccessToken(loginUserDto);

        // 알림나무 조회
        MvcResult mvcResult = mockMvc.perform(get("/api/notices")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        NoticeResponseDto noticeResponseDto = (NoticeResponseDto) objectMapper.readValue(mvcResult.getResponse().getContentAsString(), NoticeResponseDto.class);
        assertThat(noticeResponseDto.getMessages()).hasSize(8);
        assertThat(noticeResponseDto.getTotalUnreadMessageCount()).isEqualTo(4);

        // 메시지 읽을것
        mockMvc.perform(put("/api/messages/alreadyRead")
                .contentType(MediaType.APPLICATION_JSON)
                .param("messageId", String.valueOf(message1.getId()))
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();
        mockMvc.perform(put("/api/messages/alreadyRead")
                .contentType(MediaType.APPLICATION_JSON)
                .param("messageId", String.valueOf(message2.getId()))
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        MvcResult mvcResult2 = mockMvc.perform(get("/api/notices")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // 읽고나서 조회 -> 읽은만큼 개수 줄어듦
        NoticeResponseDto noticeResponseDto2 = (NoticeResponseDto) objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), NoticeResponseDto.class);
        assertThat(noticeResponseDto2.getMessages()).hasSize(6);
        assertThat(noticeResponseDto2.getTotalUnreadMessageCount()).isEqualTo(2);

        // 읽은메시지 볼 수 없음
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
    @DisplayName("유저 닉네임 변경 테스트")
    void updateUserNickname() throws Exception {
        User user = UserTest.TEST_USER;
        userRepository.save(user);

        String token = JwtTokenTest.JWT_TOKEN_TEST;
        given(jwtTokenProvider.parseToken(token)).willReturn(claims(user.getId()));


        mockMvc.perform(patch("/api/user")
                        .header("Authorization", "Bearer " + token)
                        .param("nickname", "닉네임 변경"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(user.getId()));

        User byId = userRepository.findById(user.getId()).get();
        assertThat(byId.getNickname()).isEqualTo("닉네임 변경");

    }

    private Claims claims(Long userId) {
        return new Claims() {
            @Override
            public String getIssuer() {
                return null;
            }

            @Override
            public Claims setIssuer(String iss) {
                return null;
            }

            @Override
            public String getSubject() {
                return null;
            }

            @Override
            public Claims setSubject(String sub) {
                return null;
            }

            @Override
            public String getAudience() {
                return null;
            }

            @Override
            public Claims setAudience(String aud) {
                return null;
            }

            @Override
            public Date getExpiration() {
                return null;
            }

            @Override
            public Claims setExpiration(Date exp) {
                return null;
            }

            @Override
            public Date getNotBefore() {
                return null;
            }

            @Override
            public Claims setNotBefore(Date nbf) {
                return null;
            }

            @Override
            public Date getIssuedAt() {
                return null;
            }

            @Override
            public Claims setIssuedAt(Date iat) {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public Claims setId(String jti) {
                return null;
            }

            @Override
            public <T> T get(String claimName, Class<T> requiredType) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return true;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public Object get(Object key) {
                String keyStr = (String) key;
                if (keyStr.equals("id")) {
                    return userId;
                }
                if (keyStr.equals("nickname")) {
                    return "닉네임";
                }
                if (keyStr.equals("email")) {
                    return "이메일";
                }
                return null;
            }

            @Override
            public Object put(String key, Object value) {
                return null;
            }

            @Override
            public Object remove(Object key) {
                return null;
            }

            @Override
            public void putAll(Map<? extends String, ?> m) {

            }

            @Override
            public void clear() {

            }

            @Override
            public Set<String> keySet() {
                return null;
            }

            @Override
            public Collection<Object> values() {
                return null;
            }

            @Override
            public Set<Entry<String, Object>> entrySet() {
                return null;
            }
        };
    }
}
