package com.yapp.betree.service;

import com.yapp.betree.domain.Folder;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

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
    @DisplayName("유저 나무숲 조회 - 올바르지 않은 페이지로 요청할 경우 에외가 발생한다.")
    void userForestPageErrorTest() {
        assertThatThrownBy(() -> folderService.userForest(1L, 3))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("페이지는 0 또는 1이어야 합니다.");
    }

    @Test
    @DisplayName("유저 나무숲 조회 - 요청한 페이지에 나무가 없을경우 예외가 발생한다.")
    void userForestEmptyTest() {
        Pageable pageable = PageRequest.of(1, 4);
        Slice<Folder> folders = new SliceImpl<>(Lists.newArrayList(), pageable, false);
        given(folderRepository.findByUserId(1L, pageable)).willReturn(folders);

        assertThatThrownBy(() -> folderService.userForest(1L, 1))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("해당 페이지에 나무가 존재하지 않습니다.");
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
