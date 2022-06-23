package com.yapp.betree.service;

import com.yapp.betree.dto.response.TreeResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.repository.FolderRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.yapp.betree.domain.FolderTest.TEST_SAVE_APPLE_TREE;
import static com.yapp.betree.domain.FolderTest.TEST_SAVE_DEFAULT_TREE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("FolderService 테스트")
public class FolderServiceTest {
    @Mock
    private FolderRepository folderRepository;

    @InjectMocks
    private FolderService folderService;

    @Test
    @DisplayName("유저 나무숲 조회 - 유저 id로 나무숲을 전체 조회한다.")
    void userForestTest() {
        // given
        Long userId = 1L;
        given(folderRepository.findAllByUserId(userId)).willReturn(Lists.newArrayList(TEST_SAVE_APPLE_TREE, TEST_SAVE_DEFAULT_TREE));

        // when
        List<TreeResponseDto> treeResponseDtos = folderService.userForest(userId);

        // then
        assertThat(treeResponseDtos).contains(TreeResponseDto.of(TEST_SAVE_APPLE_TREE), TreeResponseDto.of(TEST_SAVE_DEFAULT_TREE));
    }

    @Test
    @DisplayName("유저 상세 나무 조회 - treeId에 해당하는 나무가 존재하지 않으면 예외가 발생한다.")
    void userDetailTreeNotFoundTest() {
        given(folderRepository.findById(1L)).willThrow(new BetreeException(ErrorCode.TREE_NOT_FOUND));

        assertThatThrownBy(() -> folderService.userDetailTree(1L, 1L))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("나무가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("유저 상세 나무 조회 - senderId를 찾을 수 없으면 예외가 발생한다.")
    void userDetailTreeUserNotFoundTest() {

    }

    @Test
    @DisplayName("유저 상세 나무 조회 - 익명이 체크되어있을 경우 보내는 사람이 익명으로 표시된다.")
    void userDetailAnonymousTest() {

    }
}
