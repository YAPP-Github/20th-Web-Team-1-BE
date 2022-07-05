package com.yapp.betree.service;

import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.NoticeTree;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.SendUserDto;
import com.yapp.betree.dto.response.MessageResponseDto;
import com.yapp.betree.dto.response.NoticeResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.NoticeTreeRepository;
import com.yapp.betree.repository.UserRepository;
import com.yapp.betree.util.BetreeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NoticeTreeService {

    private final MessageRepository messageRepository;
    private final NoticeTreeRepository noticeTreeRepository;
    private final UserRepository userRepository;
    private final UserService userService;


    public NoticeResponseDto getUnreadMessages(Long userId) {
        NoticeTree noticeTree = noticeTreeRepository.findByUserId(userId).orElseGet(
                () -> {
                    batchNoticeTree(userId);
                    return noticeTreeRepository.findByUserId(userId).orElseThrow(() -> new BetreeException(ErrorCode.NOTICE_TREE_ERROR, "userId = " + userId));
                }
        );

        List<MessageResponseDto> messages = new ArrayList<>();
        List<Long> unreadMessageIds = Arrays.stream(noticeTree.getUnreadMessages().split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        for (Long id : unreadMessageIds) {
            if (id < 0) {
                MessageResponseDto betreeMessage = BetreeUtils.getBetreeMessage(id);
                messages.add(betreeMessage);
                continue;
            }
            Message message = messageRepository.findById(id).get();
            SendUserDto sender = userService.findBySenderId(message.getSenderId());
            messages.add(MessageResponseDto.of(message, sender));
        }
        return new NoticeResponseDto(messageRepository.findByUserIdAndAlreadyRead(userId, false).size(), messages);
    }

    @Transactional
    public void batchNoticeTree() {
        // 전체 유저 조회
        List<User> users = userRepository.findAll();
        for (User user : users) {
            noticeTree(user.getId());
        }
    }

    @Transactional
    public void batchNoticeTree(Long userId) {
        // 특정 유저 조회
        User user = userService.findById(userId).orElseThrow(() -> new BetreeException(ErrorCode.USER_NOT_FOUND, "usserId = " + userId));
        noticeTree(userId);
    }

    /**
     * userId받아서 noticeTree 테이블 갱신해주는 메서드
     *
     * @param userId
     */
    private void noticeTree(Long userId) {
        // 안읽은 메시지
        List<Message> unreadMessages = messageRepository.findByUserIdAndAlreadyRead(userId, false);

        // 안읽은 메시지 먼저 8개 리스트에 넣음
        Set<MessageResponseDto> noticeTreeMessages = new HashSet<>();
        for (Message m : unreadMessages) {
            SendUserDto sender = userService.findBySenderId(m.getSenderId());
            noticeTreeMessages.add(MessageResponseDto.of(m, sender));
        }

        // 즐겨찾기 메시지
        List<Message> favoriteMessages = messageRepository.findAllByUserIdAndFavoriteAndDelByReceiver(userId, true, false);
        for (Message m : favoriteMessages) {
            if (noticeTreeMessages.size() >= 8) {
                break; // 8개까지만 담음
            }
            SendUserDto sender = userService.findBySenderId(m.getSenderId());
            noticeTreeMessages.add(MessageResponseDto.of(m, sender));
        }

        // 비트리 제공 메시지로 8개까지 다시 채움
        long remainCount = 8 - noticeTreeMessages.size();
        for (long i = 1; i <= remainCount; i++) {
            noticeTreeMessages.add(BetreeUtils.getBetreeMessage(i));
        }

        log.info("[유저 알림나무 갱신 - 리스트 생성] userId = {}, 알림나무 = {}", userId, noticeTreeMessages);
        String unreads = noticeTreeMessages
                .stream()
                .map(MessageResponseDto::getId)
                .map(id -> String.valueOf(id))
                .collect(Collectors.joining(","));

        // 이미 존재하는 경우 변경
        Optional<NoticeTree> saveNoticeTree = noticeTreeRepository.findByUserId(userId);
        if (saveNoticeTree.isPresent()) {
            saveNoticeTree.get().resetMessages(unreads);
            log.info("[유저 알림나무 엔티티 변경] userId = {}, 알림나무 = {}", userId, saveNoticeTree);
            return;
        }
        // 새로 생성
        NoticeTree noticeTree = NoticeTree.builder()
                .unreadMessages(unreads)
                .readMessages("")
                .userId(userId)
                .build();

        log.info("[유저 알림나무 엔티티 생성] userId = {}, 알림나무 = {}", userId, noticeTree);
        noticeTreeRepository.save(noticeTree);
    }

    /**
     * 읽음처리된 메시지 알림나무 목록에 존재한다면 읽음처리
     *
     * @param userId
     * @param messageId
     */
    @Transactional
    public void updateNoticeTree(Long userId, Long messageId) {
        // 알림 나무 테이블을 갱신할건데 해당 유저의 알림나무테이블이 만들어져있지 않다면 배치를 새로 돌려 생성한다.
        NoticeTree noticeTree = noticeTreeRepository.findByUserId(userId).orElseGet(
                () -> {
                    batchNoticeTree(userId);
                    return noticeTreeRepository.findByUserId(userId).orElseThrow(() -> new BetreeException(ErrorCode.NOTICE_TREE_ERROR, "userId = " + userId));
                }
        );

        List<Long> unreadMessages = Arrays.asList(noticeTree.getUnreadMessages().split(","))
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // 알림나무 8개 항목중 안읽은 메시지 리스트에 해당 메시지가 존재하지 않는다면 return, 이후 읽음처리 과정 불필요
        if (!unreadMessages.contains(messageId)) {
            return;
        }

        // 안읽은 메시지에서 삭제
        unreadMessages.remove(messageId);
        List<Long> readMesages = Arrays.asList(noticeTree.getReadMessages().split(","))
                .stream()
                .filter(mId -> !mId.equals(""))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // 읽은 메시지 추가
        readMesages.add(messageId);
        noticeTree.updateMessages(
                unreadMessages
                        .stream()
                        .map(id -> String.valueOf(id))
                        .collect(Collectors.joining(","))
                , readMesages
                        .stream()
                        .map(id -> String.valueOf(id))
                        .collect(Collectors.joining(","))
        );
        log.info("[유저 알림나무 갱신 - 메시지 읽음처리] userId = {}, 알림나무 = {}", noticeTree);
    }
}
