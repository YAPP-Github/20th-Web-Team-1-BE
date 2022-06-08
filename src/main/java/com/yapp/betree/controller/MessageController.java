package com.yapp.betree.controller;

import com.yapp.betree.dto.request.MessageRequestDto;
import com.yapp.betree.service.MessageService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@Api
@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;


    /**
     * 칭찬 메세지 생성 (물 주기)
     * @param data       HTTP header
     * @param requestDto messageRequestDto
     */
    @PostMapping("/api/messages")
    public ResponseEntity<Object> createMessage(@RequestHeader Map<String, String> data,
                                                @RequestBody MessageRequestDto requestDto) {

        log.info("[HeaderMap] : {}", data);

        String token = data.getOrDefault("Authorization", String.valueOf(0));

        long userId;
        if (!Objects.equals(token, "0")) {
            //TO-DO
            //디코딩 하는 부분
            //나중에 util로 빼더라도.. 일단 이런 식이 괜찮은 지 적어봤습니다..
            userId = 30L;
        } else {
            userId = 1L;
        }

        messageService.createMessage(userId, requestDto);

        return ResponseEntity.noContent().build();
    }
}
