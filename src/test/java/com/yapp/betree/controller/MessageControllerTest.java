package com.yapp.betree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.LoginUserDto;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(messageController)
                .addFilters(new CharacterEncodingFilter("UTF-8", true)) // utf-8 필터 추가
                .build();
    }

    @DisplayName("메세지 생성")
    @Test
    void createMessage() throws Exception {

        User user = userRepository.findById(1L).orElseThrow(Exception::new);

        Map<String, Object> input = new HashMap<>();

        input.put("receiverId", user.getId());
        input.put("content", "컨트롤러 테스트");
        input.put("folderId", 10L);
        input.put("anonymous", false);

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("isLogin", String.valueOf(true))
                        .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("메세지함 목록 조회")
    @Test
    void getMessageList() throws Exception {

        mockMvc.perform(get("/api/messages")
                        .param("userId", String.valueOf(12L))
                        .param("page", String.valueOf(0))
                        .param("treeId", String.valueOf(19L)))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @DisplayName("열매 맺기")
    @Test
    void updateMessageOpening() throws Exception {

        List<String> idList = Arrays.asList(String.valueOf(9L),String.valueOf(10L));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("messageIdList", idList);


        mockMvc.perform(put("/api/messages/opening")
                        .param("userId", String.valueOf(1L))
                        .params(params))
                .andDo(print())
                .andExpect(status().isOk());
    }
}