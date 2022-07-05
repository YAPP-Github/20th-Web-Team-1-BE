package com.yapp.betree.controller;

import com.yapp.betree.annotation.LoginUser;
import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.dto.response.UserResponseDto;
import com.yapp.betree.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Api
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 유저 정보 조회
     *
     * @param loginUser 로그인 유저
     * @return
     */
    @ApiOperation(value = "유저 정보 조회", notes = "유저 정보 조회")
    @ApiResponses({
            @ApiResponse(code = 404, message = "[U005]회원을 찾을 수 없습니다.")
    })
    @GetMapping("/api/user")
    public ResponseEntity<UserResponseDto> userForest(@ApiIgnore @LoginUser LoginUserDto loginUser) {

        log.info("[유저] 유저 정보 조회 userId: {}", loginUser.getId());

        return ResponseEntity.ok(userService.getUser(loginUser.getId()));
    }
}
