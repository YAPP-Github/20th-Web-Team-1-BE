package com.yapp.betree.service;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.SendUserDto;
import com.yapp.betree.dto.request.TreeRequestDto;
import com.yapp.betree.dto.response.ForestResponseDto;
import com.yapp.betree.dto.response.MessageResponseDto;
import com.yapp.betree.dto.response.TreeFullResponseDto;
import com.yapp.betree.dto.response.TreeResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.util.BetreeUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.yapp.betree.domain.FolderTest.*;
import static com.yapp.betree.domain.MessageTest.TEST_SAVE_ANONYMOUS_MESSAGE;
import static com.yapp.betree.domain.MessageTest.TEST_SAVE_MESSAGE;
import static com.yapp.betree.domain.UserTest.TEST_SAVE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("FolderService 테스트")
public class FolderServiceTest {
    private static final Long TREE_ID = TEST_SAVE_DEFAULT_TREE.getId();
    private static final Long USER_ID = TEST_SAVE_USER.getId();

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    @Mock
    private FolderRepository folderRepository;

    @InjectMocks
    private FolderService folderService;

    @Test
    @DisplayName("유저 나무숲 조회 - 유저 id로 나무숲을 전체 조회한다. 기본나무는 포함되지 않는다.")
    void userForestTest() {
        // given
        given(folderRepository.findAllByUserId(USER_ID)).willReturn(Lists.newArrayList(TEST_SAVE_APPLE_TREE, TEST_SAVE_DEFAULT_TREE));
        given(userService.findById(USER_ID)).willReturn(Optional.ofNullable(TEST_SAVE_USER));

        // when
        ForestResponseDto forestResponseDto = folderService.userForest(USER_ID, USER_ID);

        // then
        assertThat(forestResponseDto.getResponseDtoList()).contains(TreeResponseDto.of(TEST_SAVE_APPLE_TREE));
    }

    @Test
    @DisplayName("유저 나무숲 조회 - 본인이 아닌 유저의 나무숲은 공개 나무만 조회된다.")
    void otherUserForestTest() {
        // given
        given(folderRepository.findAllByUserId(USER_ID)).willReturn(Lists.newArrayList(TEST_SAVE_APPLE_TREE));
        given(userService.findById(USER_ID)).willReturn(Optional.ofNullable(TEST_SAVE_USER));

        // when
        ForestResponseDto forestResponseDto = folderService.userForest(-1L, USER_ID);

        // then
        assertThat(forestResponseDto.getResponseDtoList()).contains(TreeResponseDto.of(TEST_SAVE_APPLE_TREE));
    }

    @Test
    @DisplayName("유저 상세 나무 조회 - treeId에 해당하는 나무가 존재하지 않으면 예외가 발생한다.")
    void userDetailTreeNotFoundTest() {
        given(folderRepository.findById(TREE_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> folderService.userDetailTree(USER_ID, TREE_ID, USER_ID))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("나무가 존재하지 않습니다.")
                .extracting("code").isEqualTo(ErrorCode.TREE_NOT_FOUND);
    }

    @Test
    @DisplayName("유저 상세 나무 조회 - 나무 주인과 유저가 일치하지 않으면 예외가 발생한다.")
    void userDetailTreeInvalidTest() {
        given(folderRepository.findById(TREE_ID)).willReturn(Optional.of(TEST_SAVE_DEFAULT_TREE));

        assertThatThrownBy(() -> folderService.userDetailTree(2L, TREE_ID, USER_ID))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("잘못된 접근입니다.")
                .extracting("code").isEqualTo(ErrorCode.USER_FORBIDDEN);
    }

    @Test
    @DisplayName("유저 상세 나무 조회 - senderId가 -1이면 비로그인 유저,익명으로 표시된다.")
    void userDetailTreeUserNoLoginTest() {
        // given
        List<Message> messages = Lists.newArrayList(TEST_SAVE_ANONYMOUS_MESSAGE);

        given(folderRepository.findById(TREE_ID)).willReturn(Optional.of(TEST_SAVE_DEFAULT_TREE));
        given(messageRepository.findTop8ByFolderIdAndOpeningAndDelByReceiver(TREE_ID, true, false)).willReturn(messages);
        given(userService.findBySenderId(USER_ID)).willReturn(SendUserDto.ofNoLogin());

        // when

        TreeFullResponseDto trees = folderService.userDetailTree(USER_ID, TREE_ID, USER_ID);
        MessageResponseDto message = trees.getMessages().get(0);

        // then
        assertThat(message.isAnonymous()).isTrue();
        assertThat(message.getSenderNickname()).isEqualTo("익명");
        assertThat(message.getSenderProfileImage()).isEqualTo(BetreeUtils.getImageUrl("-1"));
        assertThat(trees.getId()).isEqualTo(TEST_SAVE_DEFAULT_TREE.getId());
        assertThat(trees.getName()).isEqualTo(TEST_SAVE_DEFAULT_TREE.getName());
    }

    @Test
    @DisplayName("유저 상세 나무 조회 - senderId를 찾을 수 없으면 예외가 발생한다.")
    void userDetailTreeUserNotFoundTest() {
        // given
        List<Message> messages = Lists.newArrayList(TEST_SAVE_MESSAGE);

        given(folderRepository.findById(TREE_ID)).willReturn(Optional.of(TEST_SAVE_DEFAULT_TREE));
        given(messageRepository.findTop8ByFolderIdAndOpeningAndDelByReceiver(TREE_ID, true, false)).willReturn(messages);
        given(userService.findBySenderId(USER_ID)).willThrow(new BetreeException(ErrorCode.USER_NOT_FOUND, "senderId = " + USER_ID));

        // then
        assertThatThrownBy(() -> folderService.userDetailTree(USER_ID, TREE_ID, USER_ID))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다.")
                .extracting("code").isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("유저 상세 나무 조회 - 익명이 체크되어있을 경우 보내는 사람이 익명으로 표시된다.")
    void userDetailAnonymousTest() {
        // given
        List<Message> messages = Lists.newArrayList(TEST_SAVE_ANONYMOUS_MESSAGE);

        given(folderRepository.findById(TREE_ID)).willReturn(Optional.of(TEST_SAVE_DEFAULT_TREE));
        given(messageRepository.findTop8ByFolderIdAndOpeningAndDelByReceiver(TREE_ID, true, false)).willReturn(messages);
        given(userService.findBySenderId(USER_ID)).willReturn(SendUserDto.of(TEST_SAVE_USER));

        // when
        TreeFullResponseDto trees = folderService.userDetailTree(USER_ID, TREE_ID, USER_ID);
        MessageResponseDto message = trees.getMessages().get(0);

        // then
        assertThat(message.isAnonymous()).isTrue();
        assertThat(message.getSenderNickname()).isEqualTo("익명");
        assertThat(message.getSenderProfileImage()).isEqualTo(BetreeUtils.getImageUrl("-1"));
        assertThat(trees.getId()).isEqualTo(TEST_SAVE_DEFAULT_TREE.getId());
        assertThat(trees.getName()).isEqualTo(TEST_SAVE_DEFAULT_TREE.getName());
    }

    @Test
    @DisplayName("유저 상세 나무 조회 - prevId, nextId 0반환 테스트")
    void prevIdAndNextIdTest() {
        // given
        List<Message> messages = Lists.newArrayList(TEST_SAVE_MESSAGE);

        given(folderRepository.findById(TREE_ID)).willReturn(Optional.of(TEST_SAVE_DEFAULT_TREE));
        given(folderRepository.findTop1ByUserAndFruitIsNotAndIdGreaterThan(TEST_SAVE_USER, FruitType.DEFAULT, TREE_ID)).willReturn(Optional.empty());
        given(folderRepository.findTop1ByUserAndFruitIsNotAndIdGreaterThan(TEST_SAVE_USER, FruitType.DEFAULT, TREE_ID)).willReturn(Optional.empty());
        given(messageRepository.findTop8ByFolderIdAndOpeningAndDelByReceiver(TREE_ID, true, false)).willReturn(messages);
        given(userService.findBySenderId(USER_ID)).willReturn(SendUserDto.of(TEST_SAVE_USER));

        // when
        TreeFullResponseDto trees = folderService.userDetailTree(USER_ID, TREE_ID, USER_ID);

        // then
        assertThat(trees.getPrevId()).isEqualTo(0L);
        assertThat(trees.getNextId()).isEqualTo(0L);
    }

    @Test
    @DisplayName("유저 상세 나무 조회 - 비공개나무 조회시 예외 반환한다.")
    void userDetailTreeOpeningFalseTest() {
        // given
        List<Message> messages = Lists.newArrayList(TEST_SAVE_MESSAGE);

        given(folderRepository.findById(TREE_ID)).willReturn(Optional.of(TEST_SAVE_DEFAULT_TREE));

        // when
        assertThatThrownBy(() -> folderService.userDetailTree(USER_ID, TREE_ID, -1L))
                .isInstanceOf(BetreeException.class)
                .extracting("code").isEqualTo(ErrorCode.TREE_NOT_FOUND);
    }

    @Test
    @DisplayName("유저 나무 추가 - 나무를 추가하려는 userId가 존재하지 않으면 에외가 발생한다.")
    void createTreeUserNotFoundTest() {
        // given
        Long userId = 1L;
        given(userService.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> folderService.createTree(userId, null))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다.")
                .extracting("code").isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("유저 나무 추가 - 나무를 4개보다 많이 추가하려고 하면 예외가 발생한다.")
    void createTreeCountErrorTest() {
        // given
        Long userId = 1L;
        TreeRequestDto apple_tree = TreeRequestDto.builder()
                .name("apple tree")
                .fruitType(FruitType.APPLE)
                .build();

        given(folderRepository.countByUserIdAndFruitIsNot(userId, FruitType.DEFAULT)).willReturn(4L);

        assertThatThrownBy(() -> folderService.createTree(userId, apple_tree))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("나무는 최대 4개까지 추가 가능합니다.")
                .extracting("code").isEqualTo(ErrorCode.TREE_COUNT_ERROR);
    }

    @Test
    @DisplayName("유저 나무 추가 - 나무 추가")
    void createTreeTest() {
        // given
        Long userId = 1L;
        given(userService.findById(userId)).willReturn(Optional.of(TEST_SAVE_USER));

        TreeRequestDto treeRequestDto = TreeRequestDto.builder()
                .name("나무")
                .fruitType(FruitType.APPLE)
                .fruitType(FruitType.APPLE)
                .build();
        given(folderRepository.save(any(Folder.class))).willReturn(TEST_APPLE_TREE);

        // when
        Long treeId = folderService.createTree(userId, treeRequestDto);

        // then
        assertThat(treeId).isEqualTo(TEST_APPLE_TREE.getId());
    }


    @Test
    @DisplayName("유저 나무 - 수정하려는 회원의 userId가 존재하지 않으면 에외가 발생한다.")
    void validateTreeUserNotFoundTest() {
        // given
        Long userId = 1L;
        given(userService.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> folderService.updateTree(userId, null, null))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다.")
                .extracting("code").isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("유저 나무 - 수정하려는 나무가 존재하지 않으면 에외가 발생한다.")
    void validateTreeNotFoundTest() {
        // given
        Long userId = 1L;
        given(userService.findById(userId)).willReturn(Optional.of(TEST_SAVE_USER));

        Long treeId = 1L;
        given(folderRepository.findById(treeId)).willReturn(Optional.empty());
        assertThatThrownBy(() -> folderService.updateTree(userId, treeId, null))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("나무가 존재하지 않습니다.")
                .extracting("code").isEqualTo(ErrorCode.TREE_NOT_FOUND);
    }

    @Test
    @DisplayName("유저 나무  - 수정하려는 나무의 주인과 유저가 다르면 에외가 발생한다.")
    void validateTreeForbiddenTest() {
        // given
        User user = User.builder()
                .id(2L)
                .build();
        given(userService.findById(user.getId())).willReturn(Optional.of(user));

        Long treeId = 1L;
        given(folderRepository.findById(treeId)).willReturn(Optional.of(TEST_SAVE_APPLE_TREE));

        assertThatThrownBy(() -> folderService.updateTree(user.getId(), treeId, null))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("잘못된 접근입니다.: 유저와 나무의 주인이 일치하지 않습니다.")
                .extracting("code").isEqualTo(ErrorCode.USER_FORBIDDEN);
    }

    @Test
    @DisplayName("유저 나무 편집 - 수정하려는 나무가 Default 폴더이면 예외가 발생한다.(DEFAULT는 수정불가능)")
    void updateTreeDefaultFolderTest() {
        // given
        Long userId = 1L;
        given(userService.findById(userId)).willReturn(Optional.of(TEST_SAVE_USER));

        Long treeId = 1L;
        given(folderRepository.findById(treeId)).willReturn(Optional.of(TEST_SAVE_DEFAULT_TREE));

        TreeRequestDto treeRequestDto = TreeRequestDto.builder().name("변경 이름").fruitType(FruitType.APPLE).build();
        assertThatThrownBy(() -> folderService.updateTree(userId, treeId, treeRequestDto))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("기본 나무를 생성,변경 할 수 없습니다.")
                .extracting("code").isEqualTo(ErrorCode.TREE_DEFAULT_ERROR);
    }

    @Test
    @DisplayName("유저 나무 삭제 - 삭제하려는 나무가 기본 폴더이면 예외가 발생한다.")
    void deleteTreeTest() {
        // given
        Long userId = 1L;
        given(userService.findById(userId)).willReturn(Optional.of(TEST_SAVE_USER));

        Long treeId = 1L;
        given(folderRepository.findById(treeId)).willReturn(Optional.of(TEST_SAVE_DEFAULT_TREE));

        assertThatThrownBy(() -> folderService.deleteTree(userId, treeId))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("기본 나무를 생성,변경 할 수 없습니다.")
                .extracting("code").isEqualTo(ErrorCode.TREE_DEFAULT_ERROR);
    }
}
