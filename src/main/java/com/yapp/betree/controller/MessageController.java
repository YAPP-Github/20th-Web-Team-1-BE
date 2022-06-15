package com.yapp.betree.controller;

import com.yapp.betree.dto.request.MessageRequestDto;
import com.yapp.betree.dto.response.MessagePageResponseDto;
import com.yapp.betree.service.MessageService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
     *
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

    /**
     * 메세지함 목록 조회
     *
     * @param userId
     */
    @GetMapping("/api/messages")
    public ResponseEntity<MessagePageResponseDto> getMessageList(@RequestParam Long userId,
                                                                       @RequestParam int page) {

        log.info("[userId] : {}", userId);

        return ResponseEntity.ok(messageService.getMessageList(userId, page));
    }

    /**
     * 메세지 공개 여부 설정 (열매 맺기)
     *
     * @param userId
     * @param messageIdList 선택한 메세지 ID List
     */
    @PutMapping("/api/messages/opening")
    public ResponseEntity<Void> openingMessage(@RequestParam Long userId,
                                               @RequestParam List<Long> messageIdList) throws Exception {

        log.info("[messageIdList] : {}", messageIdList);

        messageService.updateMessageOpening(userId, messageIdList);

        return ResponseEntity.noContent().build();
    }

}
