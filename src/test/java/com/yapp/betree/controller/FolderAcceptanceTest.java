package com.yapp.betree.controller;

import com.yapp.betree.config.TestConfig;
import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FolderTest;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.User;
import com.yapp.betree.domain.UserTest;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.UserRepository;
import com.yapp.betree.service.JwtTokenTest;
import com.yapp.betree.service.oauth.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static com.yapp.betree.config.TestConfig.getClaims;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("폴더 통합 테스트")
@SpringBootTest
@Transactional
public class FolderAcceptanceTest {

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private FolderRepository folderRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("폴더 삭제 테스트")
    public void deleteFolder() throws Exception {
        User user = userRepository.save(UserTest.TEST_USER);
        user.addFolder(FolderTest.TEST_APPLE_TREE);

        Folder apple = folderRepository.findByUserIdAndFruit(user.getId(), FruitType.APPLE);
        Message message = Message.builder()
                .content("폴더 삭제")
                .senderId(UserTest.TEST_SAVE_USER.getId())
                .user(user)
                .folder(apple)
                .build();

        messageRepository.save(message);

        String token = JwtTokenTest.JWT_TOKEN_TEST;
        given(jwtTokenProvider.parseToken(token)).willReturn(getClaims(user.getId()));

        // 폴더 삭제
        mockMvc.perform(delete("/api/forest/" + apple.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(TestConfig.COOKIE_TOKEN)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNoContent());

        // 폴더, 메시지 삭제 확인
        Optional<Folder> folder = folderRepository.findById(apple.getId());
        assertThat(folder).isEmpty();
        Optional<Message> deleteMessage = messageRepository.findByIdAndUserIdAndDelByReceiver(message.getId(), user.getId(), false);
        assertThat(deleteMessage).isEmpty();
    }
}
