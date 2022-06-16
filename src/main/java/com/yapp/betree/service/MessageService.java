package com.yapp.betree.service;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.request.MessageRequestDto;
import com.yapp.betree.dto.response.MessageBoxResponseDto;
import com.yapp.betree.dto.response.MessagePageResponseDto;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final MessageRepository messageRepository;

    /**
     * 칭찬 메세지 생성 (물 주기)
     *
     * @param senderId   발신유저아이디
     * @param requestDto messageRequestDto
     */
    @Transactional
    public void createMessage(Long senderId, MessageRequestDto requestDto) {

        //수신자 유저 객체 조회
        User user = userRepository.findById(requestDto.getReceiverId()).orElseThrow(
                NoSuchElementException::new
        );

        Folder folder = folderRepository.getById(requestDto.getFolderId());

        Message message = Message.builder()
                .senderId(senderId)
                .user(user)
                .anonymous(requestDto.isAnonymous())
                .content(requestDto.getContent())
                .folder(folder)
                .build();

        //로그인 안 한 상태에서 메세지 전송시 익명 여부 true 설정
        if (senderId == 1L) {
            message.updateAnonymous();
        }

        // 본인에게 보낸 메세지일 때 읽음 여부 true 설정
        if (Objects.equals(senderId, requestDto.getReceiverId())) {
            message.updateAlreadyRead();
        }

        messageRepository.save(message);
    }

    /**
     * 메세지함 목록 조회
     *
     * @param userId
     */
    public MessagePageResponseDto getMessageList(Long userId, int page) {

        PageRequest pageRequest = PageRequest.of(page, 7, Sort.by(Sort.Direction.DESC, "createdDate"));

        boolean hasNext = messageRepository.findByUserId(userId, pageRequest).hasNext();

        List<MessageBoxResponseDto> responseDtos = messageRepository.findByUserId(userId, pageRequest)
                .stream()
                .map(message -> new MessageBoxResponseDto(message,
                        message.getUser().getNickName(),
                        message.getUser().getUserImage()))
                .collect(Collectors.toList());
        return new MessagePageResponseDto(responseDtos, hasNext);
    }

    /**
     * 선택한 메세지 공개로 설정 (열매 맺기)
     *
     * @param messageIdList 선택한 메세지 ID list
     */
    @Transactional
    public void updateMessageOpening(Long userId, List<Long> messageIdList) throws Exception {
        //선택한 개수 8개 초과면 오류
        if (messageIdList.size() > 8) {
            throw new Exception();
        }
        //이미 선택된 메세지 가져와서 false로 변경
        List<Message> messages = messageRepository.findByUserIdAndOpening(userId, true);
        for (Message m : messages) {
            m.updateOpening();
        }
        // 지금 선택된 메세지만 true로 변경
        for (Long id : messageIdList) {
            Optional<Message> message = messageRepository.findById(id);
            message.ifPresent(Message::updateOpening);
        }
    }

}
