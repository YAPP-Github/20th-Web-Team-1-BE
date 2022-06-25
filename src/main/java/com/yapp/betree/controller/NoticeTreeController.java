package com.yapp.betree.controller;

import com.yapp.betree.annotation.LoginUser;
import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.dto.response.NoticeResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.service.NoticeTreeService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api
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
    @GetMapping("/api/notice")
    public ResponseEntity<NoticeResponseDto> getUnreadMessageList(@RequestParam Long userId) {

        log.info("[userId] : {}", userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(noticeTreeService.getUnreadMessages(userId));
    }

    @GetMapping("/api/notice/batch")
    public ResponseEntity<Void> batchNoticeTree(@LoginUser LoginUserDto loginUser) {
        List<String> adminEmails = new ArrayList<>();
        adminEmails.add("bi0425@naver.com");
        adminEmails.add("happy01234@kakao.com");

        if(!adminEmails.contains(loginUser.getEmail())) {
            throw new BetreeException(ErrorCode.INVALID_INPUT_VALUE, "관리자만 사용 가능한 API");
        }

        log.info("전체유저 알림나무 갱신 user = {}", loginUser);
        noticeTreeService.batchNoticeTree();
        return ResponseEntity.ok().build();
    }
}
