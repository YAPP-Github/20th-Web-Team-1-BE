package com.yapp.betree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.betree.config.TestConfig;
import com.yapp.betree.domain.*;
import com.yapp.betree.dto.SendUserDto;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.UserRepository;
import com.yapp.betree.service.JwtTokenTest;
import com.yapp.betree.service.UserService;
import com.yapp.betree.service.oauth.JwtTokenProvider;
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

import java.util.*;

import static com.yapp.betree.config.TestConfig.getClaims;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
public class MessageAcceptanceTest {

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    // ?????????????????? ??????????????? ?????? ????????? ?????????
    @Disabled
    @Test
    @DisplayName("????????? ?????? ????????? ?????????")
    void createMessagesLoginUserTest() throws Exception {
        Folder folder = FolderTest.TEST_DEFAULT_TREE;
        User user = UserTest.TEST_USER;
        user.addFolder(folder);
        userRepository.save(user);

        String token = JwtTokenTest.JWT_TOKEN_TEST;
        given(jwtTokenProvider.parseToken(token)).willReturn(getClaims(user.getId()));

        Map<String, Object> input = new HashMap<>();

        input.put("receiverId", user.getId());
        input.put("content", "?????????10?????????~~~");
        input.put("anonymous", false);

        MvcResult mvcResult = mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();


        Long messageId = Long.parseLong(mvcResult.getResponse().getContentAsString());

        Message message = messageRepository.findByIdAndUserIdAndDelByReceiver(messageId, user.getId(), false).get();
        assertThat(message.getSenderId()).isEqualTo(user.getId());
        assertThat(message.isAnonymous()).isFalse();

        SendUserDto sender = userService.findBySenderId(message.getSenderId());
        assertThat(sender.getId()).isEqualTo(user.getId());
        assertThat(sender.getUserImage()).isEqualTo(UserTest.TEST_USER.getUserImage());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ?????? ?????????")
    public void deleteMessage() throws Exception {
        User user = userRepository.save(UserTest.TEST_USER);

        Message message = Message.builder()
                .content("?????? ??????")
                .senderId(UserTest.TEST_SAVE_USER.getId())
                .user(user)
                .build();

        messageRepository.save(message);

        String token = JwtTokenTest.JWT_TOKEN_TEST;
        given(jwtTokenProvider.parseToken(token)).willReturn(getClaims(user.getId()));

        //?????? ????????? ??????
        List<Long> messageIds = new ArrayList<>(Collections.singletonList(message.getId()));

        mockMvc.perform(delete("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(TestConfig.COOKIE_TOKEN)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(messageIds)))
                .andDo(print())
                .andExpect(status().isNoContent());

        List<Message> all = messageRepository.findAll();
        assertThat(all.get(all.size() - 1).isDelBySender()).isFalse();
        assertThat(all.get(all.size() - 1).isDelByReceiver()).isTrue();
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ??? ?????????")
    public void alreadyReadTest() throws Exception {

        User user = userRepository.save(UserTest.TEST_USER);

        Message message1 = Message.builder()
                .content("?????? ?????? ?????????")
                .senderId(UserTest.TEST_SAVE_USER.getId())
                .user(user)
                .build();
        Message message2 = Message.builder()
                .content("?????? ?????? ?????????")
                .senderId(UserTest.TEST_SAVE_USER.getId())
                .user(user)
                .alreadyRead(true)
                .build();

        messageRepository.save(message1);
        messageRepository.save(message2);

        String token = JwtTokenTest.JWT_TOKEN_TEST;
        given(jwtTokenProvider.parseToken(token)).willReturn(getClaims(user.getId()));

        // ?????? ?????? ????????? ???????????? false ??????
        mockMvc.perform(put("/api/messages/alreadyRead")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(TestConfig.COOKIE_TOKEN)
                        .header("Authorization", "Bearer " + token)
                        .param("messageId", String.valueOf(message1.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        // ?????? ?????? ????????? ???????????? true ??????
        mockMvc.perform(put("/api/messages/alreadyRead")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(TestConfig.COOKIE_TOKEN)
                        .header("Authorization", "Bearer " + token)
                        .param("messageId", String.valueOf(message2.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
