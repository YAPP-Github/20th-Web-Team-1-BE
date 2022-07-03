package com.yapp.betree.controller;

import com.yapp.betree.annotation.LoginUser;
import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.dto.response.NoticeResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.service.NoticeTreeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

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
     * @param loginUser
     * @return NoticeResponseDto
     */
    @ApiOperation(value = "유저 알림나무 조회", notes = "유저 알림나무 조회\n로그인한 유저의 알림나무를 불러옵니다.\n초기에는 안읽은 메시지 -> 즐겨찾기로 추가한 메시지 -> 비트리에서 제공하는 기본 메시지 순으로 8개가 채워집니다.")
    @ApiResponses({
            @ApiResponse(code = 400, message = "[N001]회원의 알림나무 리스트를 불러오는데 실패했습니다."),
            @ApiResponse(code = 404, message = "[U001]회원을 찾을 수 없습니다.\n" )
    })
    @GetMapping("/api/notice")
    public ResponseEntity<NoticeResponseDto> getUnreadMessageList(@ApiIgnore @LoginUser LoginUserDto loginUser) {

        log.info("[userId] : {}", loginUser.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(noticeTreeService.getUnreadMessages(loginUser.getId()));
    }

    @ApiOperation(value = "유저 알림나무 갱신", notes = "유저 알림나무 갱신 API - 백엔드 관리자만 실행 가능합니다.")
    @ApiResponses({
            @ApiResponse(code = 400, message = "[C001]관리자만 사용 가능한 API"),
    })
    @GetMapping("/api/notice/batch")
    public ResponseEntity<Void> batchNoticeTree(@ApiIgnore @LoginUser LoginUserDto loginUser) {
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
