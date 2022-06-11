package com.yapp.betree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("ForestController Test")
public class ForestControllerTest {

    @Autowired
    ForestController ForestController;
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
        this.mockMvc = MockMvcBuilders.standaloneSetup(ForestController)
                .addFilters(new CharacterEncodingFilter("UTF-8", true)) // utf-8 필터 추가
                .build();
    }

    @DisplayName("나무 추가")
    @Test
    void createTree() throws Exception {

        Map<String, Object> input = new HashMap<>();

        input.put("name", "new folder");
        input.put("fruitType", FruitType.APPLE);

        mockMvc.perform(post("/api/trees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(1L))
                        .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("나무 편집")
    @Test
    void updateTree() throws Exception {

        Map<String, Object> input = new HashMap<>();

        input.put("name", "update folder");
        input.put("fruitType", FruitType.APPLE);

        mockMvc.perform(put("/api/trees/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(1L))
                        .content(objectMapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}
