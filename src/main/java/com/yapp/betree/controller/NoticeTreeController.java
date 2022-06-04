package com.yapp.betree.controller;

import com.yapp.betree.dto.response.NoticeResponseDto;
import com.yapp.betree.service.NoticeTreeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NoticeTreeController {

    private final NoticeTreeService noticeTreeService;

    /**
     * 알림나무에 띄워 줄 메세지 리스트 조회
     * @param userId
     * @return NoticeResponseDto
     */
    @GetMapping(value = "/api/notice")
    public ResponseEntity<NoticeResponseDto> getUnreadMessageList(@RequestParam Long userId) {

        log.info("[userId] : {}", userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(noticeTreeService.getUnreadMessages(userId));
    }
}
