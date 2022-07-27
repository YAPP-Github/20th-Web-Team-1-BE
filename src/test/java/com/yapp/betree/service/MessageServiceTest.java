package com.yapp.betree.service;

import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.Message;
import com.yapp.betree.dto.SendUserDto;
import com.yapp.betree.dto.request.MessageRequestDto;
import com.yapp.betree.dto.response.MessagePageResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static com.yapp.betree.domain.FolderTest.TEST_SAVE_DEFAULT_TREE;
import static com.yapp.betree.domain.MessageTest.TEST_SAVE_ANONYMOUS_MESSAGE;
import static com.yapp.betree.domain.MessageTest.TEST_SAVE_MESSAGE;
import static com.yapp.betree.domain.UserTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService 테스트")
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private FolderRepository folderRepository;
    @InjectMocks
    private MessageService messageService;

    private final Pageable pageable = Mockito.mock(Pageable.class);

    @Nested
    @DisplayName("메세지함 목록 조회")
    class getMessageList {

        @Test
        @DisplayName("메세지함 목록 조회 - folderId 없을 경우 기본 폴더 목록 조회")
        void TotalMessageList() {

            SliceImpl<Message> messages = new SliceImpl<>(Collections.singletonList(TEST_SAVE_ANONYMOUS_MESSAGE));
            given(messageRepository.findByUserIdAndFolderIdAndDelByReceiver(TEST_SAVE_USER.getId(), TEST_SAVE_DEFAULT_TREE.getId(), false, pageable)).willReturn(messages);
            given(userService.findBySenderId(TEST_SAVE_USER.getId())).willReturn(SendUserDto.of(TEST_SAVE_USER));
            given(folderRepository.findByUserIdAndFruit(TEST_SAVE_USER.getId(), FruitType.DEFAULT)).willReturn(TEST_SAVE_DEFAULT_TREE);

            MessagePageResponseDto messageList = messageService.getMessageList(TEST_SAVE_USER.getId(), pageable, null);

            assertThat(messageList.getResponseDto().size()).isEqualTo(1);
            assertThat(messageList.getResponseDto().get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("메세지함 목록 조회 - folderId 포함시 나무별 메세지 목록 조회")
        void treeMessageList() {

            SliceImpl<Message> messages = new SliceImpl<>(Collections.singletonList(TEST_SAVE_ANONYMOUS_MESSAGE));
            given(messageRepository.findByUserIdAndFolderIdAndDelByReceiver(TEST_SAVE_USER.getId(), TEST_SAVE_DEFAULT_TREE.getId(), false, pageable)).willReturn(messages);
            given(userService.findBySenderId(TEST_SAVE_USER.getId())).willReturn(SendUserDto.of(TEST_SAVE_USER));
            MessagePageResponseDto messageList = messageService.getMessageList(TEST_SAVE_USER.getId(), pageable, TEST_SAVE_DEFAULT_TREE.getId());

            assertThat(messageList.getResponseDto().size()).isEqualTo(1);
            assertThat(messageList.getResponseDto().get(0).getId()).isEqualTo(1L);
        }
    }

    @Test
    @DisplayName("물주기 - 존재하지 않는 treeId 입력시 예외 발생")
    void treeNotFound() {

        MessageRequestDto requestDto = new MessageRequestDto(TEST_SAVE_USER.getId(), "without treeId", 10L, false);
        given(userRepository.findById(requestDto.getReceiverId())).willReturn(Optional.of(TEST_SAVE_USER));
        given(folderRepository.findById(10L)).willThrow(new BetreeException(ErrorCode.TREE_NOT_FOUND));

        assertThatThrownBy(() -> messageService.createMessage(1L, requestDto))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("나무가 존재하지 않습니다.")
                .extracting("code").isEqualTo(ErrorCode.TREE_NOT_FOUND);
    }

    @Test
    @DisplayName("열매 맺기 - 선택한 메세지 개수가 8개 초과면 예외 발생")
    void fruitCountError() {

        List<Long> messageIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

        assertThatThrownBy(() -> messageService.updateMessageOpening(TEST_SAVE_USER.getId(), messageIds))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("Invalid input value")
                .extracting("code").isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
    }

    @Test
    @DisplayName("메세지 삭제 - 존재하지 않는 messageId 입력시 예외 발생")
    void messageNotFound() {

        given(messageRepository.findByIdAndUserIdAndDelByReceiver(1L, TEST_SAVE_USER.getId(), false)).willThrow(new BetreeException(ErrorCode.MESSAGE_NOT_FOUND));

        List<Long> messageIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

        assertThatThrownBy(() -> messageService.deleteMessages(TEST_SAVE_USER.getId(), messageIds))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("메세지가 존재하지 않습니다.")
                .extracting("code").isEqualTo(ErrorCode.MESSAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("메세지 이동 - 존재하지 않는 treeId 입력시 예외 발생")
    void moveMessageNotFound() {

        given(folderRepository.findById(10L)).willThrow(new BetreeException(ErrorCode.TREE_NOT_FOUND));
        List<Long> messageIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L);

        assertThatThrownBy(() -> messageService.moveMessageFolder(TEST_SAVE_USER.getId(), messageIds, 10L))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("나무가 존재하지 않습니다.")
                .extracting("code").isEqualTo(ErrorCode.TREE_NOT_FOUND);
    }

    @Test
    @DisplayName("메세지 즐겨찾기 설정 - 존재하지 않는 messageId 입력시 예외 발생")
    void favoriteMessageNotFound() {

        given(messageRepository.findByIdAndUserIdAndDelByReceiver(10L, TEST_SAVE_USER.getId(), false)).willThrow(new BetreeException(ErrorCode.MESSAGE_NOT_FOUND));

        assertThatThrownBy(() -> messageService.updateFavoriteMessage(TEST_SAVE_USER.getId(), 10L))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("메세지가 존재하지 않습니다.")
                .extracting("code").isEqualTo(ErrorCode.MESSAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("즐겨찾기한 메세지 목록 조회")
    void getFavoriteMessages() {

        SliceImpl<Message> messages = new SliceImpl<>(Collections.singletonList(TEST_SAVE_MESSAGE));
        given(messageRepository.findByUserIdAndFavoriteAndDelByReceiver(TEST_SAVE_USER.getId(), true, false, pageable)).willReturn(messages);
        given(userService.findBySenderId(TEST_SAVE_USER.getId())).willReturn(SendUserDto.of(TEST_SAVE_USER));

        MessagePageResponseDto favoriteMessage = messageService.getFavoriteMessage(TEST_SAVE_USER.getId(), pageable);

        assertThat(favoriteMessage).isNotNull();
        assertThat(favoriteMessage.getResponseDto().get(0).isFavorite()).isTrue();
        assertThat(favoriteMessage.getResponseDto().get(0).getId()).isEqualTo(TEST_SAVE_MESSAGE.getId());
    }

    @Test
    @DisplayName("메세지 읽음 설정 - 존재하지 않는 messageId 입력시 예외 발생")
    void readMessageNotFound() {

        given(messageRepository.findByIdAndUserIdAndDelByReceiver(10L, TEST_SAVE_USER.getId(), false)).willThrow(new BetreeException(ErrorCode.MESSAGE_NOT_FOUND));

        assertThatThrownBy(() -> messageService.updateReadMessage(TEST_SAVE_USER.getId(), 10L))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("메세지가 존재하지 않습니다.")
                .extracting("code").isEqualTo(ErrorCode.MESSAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("메세지 상세 조회 - 존재하지 않는 messageId 입력시 예외 발생")
    void detailMessageNotFound() {

        given(messageRepository.findByIdAndUserIdAndDelByReceiver(10L, TEST_SAVE_USER.getId(), false)).willThrow(new BetreeException(ErrorCode.MESSAGE_NOT_FOUND));

        assertThatThrownBy(() -> messageService.getMessageDetail(TEST_SAVE_USER.getId(), 10L))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("메세지가 존재하지 않습니다.")
                .extracting("code").isEqualTo(ErrorCode.MESSAGE_NOT_FOUND);
    }
}
