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
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotBlank;

@Api
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
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
    @GetMapping("/api/users/info")
    public ResponseEntity<UserResponseDto> getUserInfo(@ApiIgnore @LoginUser LoginUserDto loginUser) {

        log.info("[유저] 유저 정보 조회 userId: {}", loginUser.getId());

        return ResponseEntity.ok(userService.getUser(loginUser.getId()));
    }

    /**
     * 유저 닉네임 변경
     *
     * @param loginUser 로그인 유저
     * @param nickname  변경할 닉네임
     * @return
     */
    @ApiOperation(value = "유저 닉네임 변경", notes = "유저 닉네임 변경")
    @ApiResponses({
            @ApiResponse(code = 404, message = "[U005]회원을 찾을 수 없습니다.")
    })
    @PutMapping("/api/users/nickname")
    public ResponseEntity<Long> updateUserNickname(@ApiIgnore @LoginUser LoginUserDto loginUser,
                                                   @RequestParam @NotBlank(message = "닉네임은 빈값일 수 없습니다.")
                                                   @Length(max = 20, message = "닉네임은 20자를 넘을 수 없습니다.") String nickname) {

        log.info("[유저 닉네임 편집] userId: {}, 변경할 닉네임: {},", loginUser.getId(), nickname);

        userService.updateUserNickname(loginUser.getId(), nickname);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loginUser.getId());
    }
}
