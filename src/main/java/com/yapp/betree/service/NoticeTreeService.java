package com.yapp.betree.service;

import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.NoticeTree;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.SendUserDto;
import com.yapp.betree.dto.response.MessageResponseDto;
import com.yapp.betree.dto.response.NoticeResponseDto;
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
        Optional<NoticeTree> noticeTree = noticeTreeRepository.findByUserId(userId);
        List<MessageResponseDto> messages = new ArrayList<>();

        if (!noticeTree.isPresent()) {
            return new NoticeResponseDto(34, messages);
        }

        List<Long> unreadMessageIds = Arrays.stream(noticeTree.get().getUnreadMessages().split(","))
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
            // 안읽은 메시지
            List<Message> unreadMessages = messageRepository.findByUserIdAndAlreadyRead(user.getId(), false);

            // 안읽은 메시지 먼저 8개 리스트에 넣음
            Set<MessageResponseDto> noticeTreeMessages = new HashSet<>();
            for (Message m : unreadMessages) {
                SendUserDto sender = userService.findBySenderId(m.getSenderId());
                noticeTreeMessages.add(MessageResponseDto.of(m, sender));
            }

            // 즐겨찾기 메시지
            List<Message> favoriteMessages = messageRepository.findAllByUserIdAndFavorite(user.getId(), true);
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

            log.info("[유저 알림나무 갱신 - 리스트 생성] userId = {}, 알림나무 = {}", user.getId(), noticeTreeMessages);
            String unreads = noticeTreeMessages
                    .stream()
                    .map(MessageResponseDto::getId)
                    .map(id -> String.valueOf(id))
                    .collect(Collectors.joining(","));

            // 이미 존재하는 경우 변경
            Optional<NoticeTree> saveNoticeTree = noticeTreeRepository.findByUserId(user.getId());
            if (saveNoticeTree.isPresent()) {
                saveNoticeTree.get().updateMessages(unreads);
                log.info("[유저 알림나무 엔티티 변경] userId = {}, 알림나무 = {}", user.getId(), saveNoticeTree);
                return;
            }
            // 새로 생성
            NoticeTree noticeTree = NoticeTree.builder()
                    .unreadMessages(unreads)
                    .userId(user.getId())
                    .build();

            log.info("[유저 알림나무 엔티티 생성] userId = {}, 알림나무 = {}", user.getId(), noticeTree);
            noticeTreeRepository.save(noticeTree);
        }
    }
}
