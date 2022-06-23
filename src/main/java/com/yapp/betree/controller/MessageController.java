package com.yapp.betree.controller;

import com.yapp.betree.annotation.LoginUser;
import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.dto.request.MessageRequestDto;
import com.yapp.betree.dto.response.MessagePageResponseDto;
import com.yapp.betree.exception.BetreeException;
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
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

import static com.yapp.betree.exception.ErrorCode.*;

@Api
@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 칭찬 메시지 생성 (물주기)
     *
     * @param isLogin
     * @param loginUser
     * @param requestDto
     * @return
     */
    @ApiOperation(value = "물주기", notes = "칭찬 메시지 생성")
    @ApiResponses({
            @ApiResponse(code = 400, message = "[C001]Invalid input value(isLogin과 로그인 유저 값 불일치 ex.isLogin = false지만 loginUser는 존재)\n" +
                    "[F002]해당 페이지에 나무가 존재하지 않습니다."),
            @ApiResponse(code = 404, message = "[U001]회원을 찾을 수 없습니다.\n" +
                    "[T001]나무가 존재하지 않습니다.")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/messages")
    public ResponseEntity<Object> createMessage(@RequestParam boolean isLogin,
                                                @ApiIgnore @LoginUser LoginUserDto loginUser,
                                                @Valid @RequestBody MessageRequestDto requestDto) {

        log.info("물주기 요청 내용 : {}", requestDto);

        long userId;
        if (isLogin && loginUser.getId() != null) {
            userId = loginUser.getId();
        } else if (!isLogin && loginUser.getId() == null) {
            userId = 1L;

            //TODO 비로그인 물 주기
        } else {
            //로그인 여부와 로그인 유저 값이 맞지 않음
            throw new BetreeException(INVALID_INPUT_VALUE, "isLogin = " + isLogin + ", loginUser = " + loginUser);
        }

        messageService.createMessage(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 메세지함 목록 조회
     * - treeId 입력시 폴더별 조회
     *
     * @param loginUser
     * @param page
     * @param treeId
     * @return
     */
    @ApiOperation(value = "메세지함 목록 조회", notes = "유저의 메세지함 목록 조회- treeId 입력시 폴더별 조회 / 없으면 전체 조회")
    @ApiResponses({
            @ApiResponse(code = 401, message = "[U006]로그인이 필요한 서비스입니다."),
            @ApiResponse(code = 404, message = "[T001]나무가 존재하지 않습니다.")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/messages")
    public ResponseEntity<MessagePageResponseDto> getMessageList(@ApiIgnore @LoginUser LoginUserDto loginUser,
                                                                 @RequestParam int page,
                                                                 @RequestParam(required = false) Long treeId) {
        if (loginUser.getId() == null) {
            throw new BetreeException(USER_REQUIRE_LOGIN, "loginUser Id = " + loginUser.getId());
        }

        log.info("[userId] : {}", loginUser.getId());

        return ResponseEntity.ok(messageService.getMessageList(loginUser.getId(), page, treeId));
    }

    /**
     * 메세지 공개 여부 설정 (열매 맺기)
     *
     * @param loginUser
     * @param messageIdList 선택한 메세지 ID List
     */
    @ApiOperation(value = "열매 맺기", notes = "메세지 공개 여부 설정")
    @ApiResponses({
            @ApiResponse(code = 400, message = "[C001]Invalid input value (열매 선택 개수 오류- 8개 초과)"),
            @ApiResponse(code = 401, message = "[U006]로그인이 필요한 서비스입니다.")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/api/messages/opening")
    public ResponseEntity<Object> openingMessage(@ApiIgnore @LoginUser LoginUserDto loginUser,
                                                 @RequestParam List<Long> messageIdList) {

        if (loginUser.getId() == null) {
            throw new BetreeException(USER_REQUIRE_LOGIN, "loginUser Id = " + loginUser.getId());
        }

        log.info("[messageIdList] : {}", messageIdList);

        messageService.updateMessageOpening(loginUser.getId(), messageIdList);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
