package com.yapp.betree.service;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.request.MessageRequestDto;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final MessageRepository messageRepository;

    /**
     * 칭찬 메세지 생성 (물 주기)
     * @param senderId   발신유저아이디
     * @param requestDto messageRequestDto
     */
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
}
