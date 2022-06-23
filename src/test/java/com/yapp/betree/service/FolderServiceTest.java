package com.yapp.betree.service;

import com.yapp.betree.domain.Message;
import com.yapp.betree.dto.response.MessageResponseDto;
import com.yapp.betree.dto.response.TreeFullResponseDto;
import com.yapp.betree.dto.response.TreeResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.yapp.betree.domain.FolderTest.TEST_SAVE_APPLE_TREE;
import static com.yapp.betree.domain.FolderTest.TEST_SAVE_DEFAULT_TREE;
import static com.yapp.betree.domain.MessageTest.TEST_SAVE_ANONYMOUS_MESSAGE;
import static com.yapp.betree.domain.MessageTest.TEST_SAVE_MESSAGE;
import static com.yapp.betree.domain.UserTest.TEST_SAVE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("FolderService 테스트")
public class FolderServiceTest {
    private static final Long TREE_ID = TEST_SAVE_DEFAULT_TREE.getId();
    private static final Long USER_ID = TEST_SAVE_USER.getId();

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FolderRepository folderRepository;

    @InjectMocks
    private FolderService folderService;

    @Test
    @DisplayName("유저 나무숲 조회 - 유저 id로 나무숲을 전체 조회한다.")
    void userForestTest() {
        // given
        given(folderRepository.findAllByUserId(USER_ID)).willReturn(Lists.newArrayList(TEST_SAVE_APPLE_TREE, TEST_SAVE_DEFAULT_TREE));

        // when
        List<TreeResponseDto> treeResponseDtos = folderService.userForest(USER_ID);

        // then
        assertThat(treeResponseDtos).contains(TreeResponseDto.of(TEST_SAVE_APPLE_TREE), TreeResponseDto.of(TEST_SAVE_DEFAULT_TREE));
    }

    @Test
    @DisplayName("유저 상세 나무 조회 - treeId에 해당하는 나무가 존재하지 않으면 예외가 발생한다.")
    void userDetailTreeNotFoundTest() {
        given(folderRepository.findById(TREE_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> folderService.userDetailTree(USER_ID, TREE_ID))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("나무가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("유저 상세 나무 조회 - 나무 주인과 유저가 일치하지 않으면 예외가 발생한다.")
    void userDetailTreeInvalidTest() {
        given(folderRepository.findById(TREE_ID)).willReturn(Optional.of(TEST_SAVE_DEFAULT_TREE));

        assertThatThrownBy(() -> folderService.userDetailTree(2L, TREE_ID))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("Invalid input");
    }

    @Test
    @DisplayName("유저 상세 나무 조회 - senderId를 찾을 수 없으면 예외가 발생한다.")
    void userDetailTreeUserNotFoundTest() {
        // given
        List<Message> messages = Lists.newArrayList(TEST_SAVE_MESSAGE);

        given(folderRepository.findById(TREE_ID)).willReturn(Optional.of(TEST_SAVE_DEFAULT_TREE));
        given(messageRepository.findTop8ByFolderIdAndOpening(TREE_ID, true)).willReturn(messages);
        given(userRepository.findById(USER_ID)).willReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> folderService.userDetailTree(USER_ID, TREE_ID))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("유저 상세 나무 조회 - 익명이 체크되어있을 경우 보내는 사람이 익명으로 표시된다.")
    void userDetailAnonymousTest() {
        // given
        List<Message> messages = Lists.newArrayList(TEST_SAVE_ANONYMOUS_MESSAGE);

        given(folderRepository.findById(TREE_ID)).willReturn(Optional.of(TEST_SAVE_DEFAULT_TREE));
        given(messageRepository.findTop8ByFolderIdAndOpening(TREE_ID, true)).willReturn(messages);
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(TEST_SAVE_USER));

        // when
        TreeFullResponseDto trees = folderService.userDetailTree(USER_ID, TREE_ID);
        MessageResponseDto message = trees.getMessages().get(0);

        // then
        assertThat(message.isAnonymous()).isTrue();
        assertThat(message.getSenderNickname()).isEqualTo("익명");
        assertThat(message.getSenderProfileImage()).isEqualTo("기본 이미지");
        assertThat(trees.getId()).isEqualTo(TEST_SAVE_DEFAULT_TREE.getId());
        assertThat(trees.getName()).isEqualTo(TEST_SAVE_DEFAULT_TREE.getName());
    }

    @Test
    @DisplayName("유저 상세 나무 조회 - prevId, nextId 0반환 테스트")
    void prevIdAndNextIdTest() {
        // given
        List<Message> messages = Lists.newArrayList(TEST_SAVE_MESSAGE);

        given(folderRepository.findById(TREE_ID)).willReturn(Optional.of(TEST_SAVE_DEFAULT_TREE));
        given(folderRepository.findTop1ByUserAndIdGreaterThan(TEST_SAVE_USER, TREE_ID)).willReturn(Optional.empty());
        given(folderRepository.findTop1ByUserAndIdGreaterThan(TEST_SAVE_USER, TREE_ID)).willReturn(Optional.empty());
        given(messageRepository.findTop8ByFolderIdAndOpening(TREE_ID, true)).willReturn(messages);
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(TEST_SAVE_USER));

        // when
        TreeFullResponseDto trees = folderService.userDetailTree(USER_ID, TREE_ID);

        // then
        assertThat(trees.getPrevId()).isEqualTo(0L);
        assertThat(trees.getNextId()).isEqualTo(0L);
    }
}
