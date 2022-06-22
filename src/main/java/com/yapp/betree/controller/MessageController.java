package com.yapp.betree.controller;

import com.yapp.betree.dto.request.MessageRequestDto;
import com.yapp.betree.dto.response.MessagePageResponseDto;
import com.yapp.betree.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
     * @param requestDto messageRequestDto
     */
    @ApiOperation(value = "칭찬 메시지 생성(물주기)", notes = "칭찬 메시지 생성(물주기)")
    @ApiResponses({
            @ApiResponse(code = 400, message = "[C001]Invalid input value\n" +
                    "[F002]해당 페이지에 나무가 존재하지 않습니다."),
            @ApiResponse(code = 404, message = "[U001]회원을 찾을 수 없습니다.\n" +
                    "[T001]나무가 존재하지 않습니다.")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/messages")
    public ResponseEntity<Object> createMessage(@RequestParam boolean isLogin,
                                                @Valid @RequestBody MessageRequestDto requestDto) {

        log.info("물주기 요청 : {}", requestDto);

        long userId;
        if (isLogin) {
            userId = 30L;
        } else {
            userId = 1L;
        }

        messageService.createMessage(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
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
    public ResponseEntity<Object> openingMessage(@RequestParam Long userId,
                                               @RequestParam List<Long> messageIdList) throws Exception {

        log.info("[messageIdList] : {}", messageIdList);

        messageService.updateMessageOpening(userId, messageIdList);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
