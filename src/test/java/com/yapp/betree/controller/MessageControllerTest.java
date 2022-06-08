package com.yapp.betree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.User;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.UserRepository;
import com.yapp.betree.service.MessageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("MessageController Test")
class MessageControllerTest {

    @Autowired
    MessageController messageController;
    @Autowired
    MessageService messageService;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    private User user;
    private Folder folder;


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(messageController)
                .addFilters(new CharacterEncodingFilter("UTF-8", true)) // utf-8 필터 추가
                .build();
    }

    @BeforeEach
    void init() {

        user = User.builder()
                .nickName("user")
                .email("user@user.com")
                .url("testUrl")
                .lastAccessTime(LocalDateTime.now())
                .userImage("123")
                .build();

        userRepository.save(user);

        folder = Folder.builder()
                .fruit(FruitType.DEFAULT)
                .name("default")
                .user(user)
                .level(0L)
                .build();

        folderRepository.save(folder);
    }

    @DisplayName("메세지 생성")
    @Test
    void createMessage() throws Exception {

        Map<String, Object> input = new HashMap<>();

        input.put("receiverId", user.getId());
        input.put("content", "컨트롤러 테스트");
        input.put("folderName", "default");
        input.put("anonymous", false);

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}