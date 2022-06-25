package com.yapp.betree.service;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.request.MessageRequestDto;
import com.yapp.betree.dto.response.MessagePageResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import java.util.*;

import static com.yapp.betree.domain.UserTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService 테스트")
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FolderRepository folderRepository;
    @InjectMocks
    private MessageService messageService;

    private User user1;
    private Folder appleFolder;

    @BeforeEach
    void setup() {

        //테스트 유저
        given(userRepository.findById(TEST_USER.getId())).willReturn(Optional.of(TEST_SAVE_USER));
        user1 = userRepository.findById(TEST_USER.getId()).get();

        //테스트 폴더
        appleFolder = Folder.builder().id(1L).fruit(FruitType.APPLE).level(0L).user(user1).build();

    }

    @Nested
    @DisplayName("메세지함 목록 조회")
    class getMessageList {

        private static final int PAGE_SIZE = 7;
        private static final int page = 0;

        @Test
        @DisplayName("메세지함 목록 조회 - 존재하지 않는 treeId 입력시 예외 발생")
        void treeNotFound() {

            PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdDate"));
            Slice<Message> messages = Mockito.mock(Slice.class);

            given(messageRepository.findByUserId(user1.getId(), pageRequest)).willReturn(messages);
            given(folderRepository.findById(1L)).willThrow(new BetreeException(ErrorCode.TREE_NOT_FOUND));

            assertThatThrownBy(() -> messageService.getMessageList(user1.getId(), page, 1L))
                    .isInstanceOf(BetreeException.class)
                    .hasMessageContaining("나무가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("메세지함 목록 조회 - folderId 없을 경우 전체 목록 조회")
        void TotalMessageList() {
            //given
            PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdDate"));
            SliceImpl<Message> messages = new SliceImpl<>(Collections.emptyList());

            given(messageRepository.findByUserId(user1.getId(), pageRequest)).willReturn(messages);

            //when
            MessagePageResponseDto messageList = messageService.getMessageList(user1.getId(), page, null);

            //then
            assertThat(messageList).isNotNull();
            assertThat(messageList.isHasNext()).isFalse();
            assertThat(messageList.getResponseDto()).isInstanceOf(ArrayList.class);
        }

        @Test
        @DisplayName("메세지함 목록 조회 - folderId 포함시 나무별 메세지 목록 조회")
        void treeMessageList() {
            //given
            PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdDate"));
            SliceImpl<Message> messages = new SliceImpl<>(Collections.emptyList());

            given(messageRepository.findByUserId(user1.getId(), pageRequest)).willReturn(messages);

            given(folderRepository.findById(appleFolder.getId())).willReturn(Optional.of(appleFolder));

            given(messageRepository.findByUserIdAndFolderId(user1.getId(), appleFolder.getId(), pageRequest)).willReturn(messages);

            //when
            MessagePageResponseDto messageList = messageService.getMessageList(user1.getId(), page, appleFolder.getId());

            //then
            assertThat(messageList).isNotNull();
            assertThat(messageList.isHasNext()).isFalse();
            assertThat(messageList.getResponseDto()).isInstanceOf(ArrayList.class);
        }
    }

    @Test
    @DisplayName("물주기 - 존재하지 않는 treeId 입력시 예외 발생")
    void treeNotFound() {

        MessageRequestDto requestDto = new MessageRequestDto(user1.getId(), "without treeId", 10L, false);
        given(userRepository.findById(requestDto.getReceiverId())).willReturn(Optional.of(user1));
        given(folderRepository.findById(10L)).willThrow(new BetreeException(ErrorCode.TREE_NOT_FOUND));

        assertThatThrownBy(() -> messageService.createMessage(1L, requestDto))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("나무가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("열매 맺기 - 선택한 메세지 개수가 8개 초과면 예외 발생")
    void fruitCountError() {

        List<Long> messageIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

        assertThatThrownBy(() -> messageService.updateMessageOpening(user1.getId(), messageIds))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("Invalid input value");
    }

    @Test
    @DisplayName("메세지 삭제 - 존재하지 않는 messageId 입력시 예외 발생")
    void messageNotFound() {

        given(messageRepository.findByIdAndUserId(user1.getId(), 10L)).willThrow(new BetreeException(ErrorCode.MESSAGE_NOT_FOUND));

        List<Long> messageIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

        assertThatThrownBy(() -> messageService.deleteMessages(user1.getId(), messageIds))
                .isInstanceOf(BetreeException.class)
                .hasMessageContaining("메세지가 존재하지 않습니다.");
    }
}
