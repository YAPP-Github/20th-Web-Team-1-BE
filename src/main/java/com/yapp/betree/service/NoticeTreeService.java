package com.yapp.betree.service;

import com.yapp.betree.dto.response.MessageResponseDto;
import com.yapp.betree.dto.response.NoticeResponseDto;
import com.yapp.betree.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeTreeService {

    private final MessageRepository messageRepository;


    public NoticeResponseDto getUnreadMessages(Long userId) {
        List<MessageResponseDto> messages = new ArrayList<>();
        return new NoticeResponseDto(34, messages);
    }
}
