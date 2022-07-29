package com.yapp.betree.service;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.SendUserDto;
import com.yapp.betree.dto.request.MessageRequestDto;
import com.yapp.betree.dto.request.OpeningRequestDto;
import com.yapp.betree.dto.response.MessageBoxResponseDto;
import com.yapp.betree.dto.response.MessageDetailResponseDto;
import com.yapp.betree.dto.response.MessagePageResponseDto;
import com.yapp.betree.dto.response.TreeResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yapp.betree.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final FolderRepository folderRepository;
    private final MessageRepository messageRepository;
    private final NoticeTreeService noticeTreeService;

    /**
     * 칭찬 메세지 생성 (물 주기)
     *
     * @param senderId   발신유저아이디
     * @param requestDto messageRequestDto
     * @return
     */
    @Transactional
    public Long createMessage(Long senderId, MessageRequestDto requestDto) {

        //수신자 유저 객체 조회
        User user = userRepository.findById(requestDto.getReceiverId()).orElseThrow(() -> new BetreeException(USER_NOT_FOUND, "receiverId = " + requestDto.getReceiverId()));

        Folder folder;
        if (requestDto.getFolderId() == null) {
            //상대방 디폴트 폴더로 지정
            folder = folderRepository.findByUserIdAndFruit(requestDto.getReceiverId(), FruitType.DEFAULT);
        } else {
            folder = folderRepository.findById(requestDto.getFolderId()).orElseThrow(() -> new BetreeException(TREE_NOT_FOUND, "tree = " + requestDto.getFolderId()));
        }

        Message message = Message.builder()
                .senderId(senderId)
                .user(user)
                .anonymous(requestDto.isAnonymous())
                .content(requestDto.getContent())
                .folder(folder)
                .build();


        //로그인 안 한 상태에서 메세지 전송시 익명 여부 true 설정
        if (senderId == -1L && !message.isAnonymous()) {
            message.updateAnonymous();
        }


        return messageRepository.save(message).getId();
    }

    /**
     * 메세지 목록 조회
     * - treeId 입력시 폴더별 조회
     *
     * @param userId
     * @param pageable
     * @param treeId
     * @return
     */
    public MessagePageResponseDto getMessageList(Long userId, Pageable pageable, Long treeId) {

        Slice<Message> messages;
        if (treeId == null) {
            //기본 폴더 목록 조회
            Long defaultTreeId = folderRepository.findByUserIdAndFruit(userId, FruitType.DEFAULT).getId();
            messages = messageRepository.findByUserIdAndFolderIdAndDelByReceiver(userId, defaultTreeId, false, pageable);
        } else {
            //해당 폴더 메세지 목록 조회
            messages = messageRepository.findByUserIdAndFolderIdAndDelByReceiver(userId, treeId, false, pageable);
        }

        List<MessageBoxResponseDto> responseDtos = new ArrayList<>();

        for (Message message : messages) {
            SendUserDto sender = userService.findBySenderId(message.getSenderId());
            responseDtos.add(MessageBoxResponseDto.of(message, sender));
        }
        return new MessagePageResponseDto(responseDtos, messages.hasNext());
    }

    /**
     * 선택한 메세지 공개로 설정 (열매 맺기)
     *
     * @param userId
     * @param dto
     */
    @Transactional
    public void updateMessageOpening(Long userId, OpeningRequestDto dto) {
        //선택한 개수 8개 초과면 오류
        if (dto.getMessageIds().size() > 8) {
            throw new BetreeException(ErrorCode.INVALID_INPUT_VALUE, "열매로 맺을 수 있는 메세지 개수는 최대 8개입니다.");
        }
        //이미 선택된 메세지 가져와서 false로 변경
        messageRepository.findByUserIdAndOpeningAndDelByReceiverAndFolderId(userId, true, false, dto.getTreeId()).forEach(Message::updateOpening);

        // 지금 선택된 메세지만 true로 변경
        for (Long id : dto.getMessageIds()) {
            messageRepository.findById(id).orElseThrow(() -> new BetreeException(MESSAGE_NOT_FOUND, "messageId = " + id)).updateOpening();
        }
    }

    /**
     * 메세지 삭제
     *
     * @param userId
     * @param messageIds
     */
    @Transactional
    public void deleteMessages(Long userId, List<Long> messageIds) {

        Folder defaultFolder = folderRepository.findByUserIdAndFruit(userId, FruitType.DEFAULT);
        // 메세지의 발신자, 수신자 확인 후 알맞는 삭제 여부 필드 변경
        messageIds.forEach(messageId -> {
            Message message = messageRepository.findByIdAndUserIdAndDelByReceiver(messageId, userId, false).orElseThrow(() -> new BetreeException(MESSAGE_NOT_FOUND, "messageId = " + messageId + "userId = " + userId));
            message.updateDeleteStatus(userId, defaultFolder);

            // 수신자, 발신자 모두 삭제 true 이면 db 삭제
            if (message.isDelByReceiver() && message.isDelBySender()) {
                messageRepository.delete(message);
            }
        });
    }

    /**
     * 메세지 이동
     *
     * @param userId
     * @param messageIds
     * @param treeId
     */
    @Transactional
    public void moveMessageFolder(Long userId, List<Long> messageIds, Long treeId) {

        Folder folder = folderRepository.findById(treeId).orElseThrow(() -> new BetreeException(TREE_NOT_FOUND, "treeId = " + treeId));

        messageIds.forEach(messageId -> messageRepository.findByIdAndUserIdAndDelByReceiver(messageId, userId, false).orElseThrow(() -> new BetreeException(MESSAGE_NOT_FOUND, "messageId =" + messageId + "userId = " + userId))
                .updateFolder(folder));
    }

    /**
     * 메세지 즐겨찾기 상태 변경
     *
     * @param userId
     * @param messageId
     */
    @Transactional
    public void updateFavoriteMessage(Long userId, Long messageId) {

        messageRepository.findByIdAndUserIdAndDelByReceiver(messageId, userId, false).orElseThrow(() -> new BetreeException(MESSAGE_NOT_FOUND, "messageId =" + messageId))
                .updateFavorite();
    }

    /**
     * 즐겨찾기한 메세지 목록 조회
     *
     * @param userId
     * @param pageable
     * @return
     */
    @Transactional
    public MessagePageResponseDto getFavoriteMessage(Long userId, Pageable pageable) {

        //다음 페이지 존재 여부
        Slice<Message> messages = messageRepository.findByUserIdAndFavoriteAndDelByReceiver(userId, true, false, pageable);

        List<MessageBoxResponseDto> responseMessages = messages
                .stream()
                .map(message -> {
                    SendUserDto sender = userService.findBySenderId(message.getSenderId());
                    return MessageBoxResponseDto.of(message, sender);
                })
                .collect(Collectors.toList());

        return new MessagePageResponseDto(responseMessages, messages.hasNext());
    }

    /**
     * 메세지 읽음 여부 상태 변경
     *
     * @param userId
     * @param messageId
     */
    @Transactional
    public Boolean updateReadMessage(Long userId, Long messageId) {

        boolean result = true;

        if (messageId > 0L) { // 비트리에서 보내준 메시지(id가 음수)일 경우는 읽음처리 제외
            Message message = messageRepository.findByIdAndUserIdAndDelByReceiver(messageId, userId, false).orElseThrow(() -> new BetreeException(MESSAGE_NOT_FOUND, "messageId =" + messageId));
            if (!message.isAlreadyRead()) {
                message.updateAlreadyRead();
                result = false;
            }
        }
        noticeTreeService.updateNoticeTree(userId, messageId);
        return result;
    }

    /**
     * 메세지 상세 조회
     *
     * @param userId
     * @param messageId
     * @return
     */
    public MessageDetailResponseDto getMessageDetail(Long userId, Long messageId, boolean favorite) {

        Message message = messageRepository.findByIdAndUserIdAndDelByReceiver(messageId, userId, false).orElseThrow(() -> new BetreeException(MESSAGE_NOT_FOUND, "messageId =" + messageId));

        MessageBoxResponseDto boxResponseDto = MessageBoxResponseDto.of(message, userService.findBySenderId(message.getSenderId()));

        Folder folder = message.getFolder();

        Long prevId;
        Long nextId;
        // 즐겨찾기 메세지는 즐겨찾기 메세지끼리 이전, 다음 메세지 보여주기
        if (favorite) {
            prevId = messageRepository.findTop1ByUserIdAndFavoriteAndDelByReceiverAndIdLessThanOrderByIdDesc(userId, true, false, message.getId())
                    .map(Message::getId)
                    .orElse(0L);
            nextId = messageRepository.findTop1ByUserIdAndFavoriteAndDelByReceiverAndIdGreaterThan(userId, true, false, message.getId())
                    .map(Message::getId)
                    .orElse(0L);
        } else {
            prevId = messageRepository.findTop1ByUserIdAndFolderIdAndDelByReceiverAndIdLessThanOrderByIdDesc(userId, folder.getId(), false, messageId)
                    .map(Message::getId)
                    .orElse(0L);
            nextId = messageRepository.findTop1ByUserIdAndFolderIdAndDelByReceiverAndIdGreaterThan(userId, folder.getId(), false, messageId)
                    .map(Message::getId)
                    .orElse(0L);
        }

        return MessageDetailResponseDto.of(boxResponseDto, TreeResponseDto.of(folder), prevId, nextId);
    }

    /**
     * 회원가입한 유저 기본메시지 생성
     *
     * @param user
     */
    @Transactional
    public void sendWelcomeMessage(User user) {
        log.info("[회원가입] 유저 웰컴메시지 생성 userId = {}", user.getId());
        messageRepository.save(Message.generateWelcomeMessage(user, user.getFolders().get(0)));
    }
}
