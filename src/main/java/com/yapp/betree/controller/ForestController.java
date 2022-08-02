package com.yapp.betree.controller;

import com.yapp.betree.annotation.LoginUser;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.dto.request.TreeRequestDto;
import com.yapp.betree.dto.response.ForestResponseDto;
import com.yapp.betree.dto.response.TreeFullResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.service.FolderService;
import com.yapp.betree.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api
@RestController
@RequiredArgsConstructor
@Slf4j
public class ForestController {

    private final FolderService folderService;
    private final UserService userService;

    /**
     * 유저 나무숲 조회 [비로그인 유저 요청 가능]
     *
     * @param userId
     * @return ForestResponseDto
     */
    @ApiOperation(value = "유저 나무숲 조회[비로그인 가능]", notes = "유저 나무숲 조회")
    @ApiResponses({
            @ApiResponse(code = 404, message = "[U005]회원을 찾을 수 없습니다.")
    })
    @GetMapping("/api/forest")
    public ResponseEntity<ForestResponseDto> userForest(@ApiIgnore @LoginUser LoginUserDto loginUser,
                                                        @RequestParam Long userId) {
        log.info("[나무숲] 유저 나무숲 조회 userId: {}, loginUserId: {}", userId, loginUser.getId());
        if (!userService.isExist(userId)) {
            throw new BetreeException(ErrorCode.USER_NOT_FOUND, "userId = " + userId);
        }
        return ResponseEntity.ok(folderService.userForest(loginUser.getId(), userId));
    }

    /**
     * 유저 상세 나무 조회 [비로그인 유저 요청 가능]
     *
     * @param userId
     * @param treeId
     * @return TreeFullResponseDto
     */
    @ApiOperation(value = "유저 상세 나무 조회[비로그인 가능]", notes = "유저 상세 나무 조회\n" +
            "해당하는 나무의 prevId, nextId가 존재하지 않을경우 id는 0입니다.")
    @ApiResponses({
            @ApiResponse(code = 404, message = "[T001]나무가 존재하지 않습니다.\n" +
                    "[U005]회원을 찾을 수 없습니다."),
            @ApiResponse(code = 403, message = "[U006]잘못된 접근입니다. 유저와 나무의 주인이 일치하지 않습니다.")
    })
    @GetMapping("/api/forest/{treeId}")
    public ResponseEntity<TreeFullResponseDto> userDetailTree(
            @ApiIgnore @LoginUser LoginUserDto loginUser,
            @RequestParam Long userId,
            @PathVariable Long treeId) {
        log.info("[나무숲] 유저 상세 나무 조회 userId: {}, treeId: {}, 로그인 유저 정보 : {}", userId, treeId, loginUser);

        if (!userService.isExist(userId)) {
            throw new BetreeException(ErrorCode.USER_NOT_FOUND, "userId = " + userId);
        }
        return ResponseEntity.ok(folderService.userDetailTree(userId, treeId, loginUser.getId()));
    }

    /**
     * 유저 나무 추가
     *
     * @param treeRequestDto 나무(이름,타입) DTO
     * @return (Long) treeId 생성된 나무 아이디
     */
    @ApiOperation(value = "유저 나무 추가", notes = "유저 나무 추가")
    @ApiResponses({
            @ApiResponse(code = 400, message = "[C004]잘못된 ENUM값 입니다.\n" +
                    "[C001]Invalid input value (나무 이름은 빈 값일 수 없습니다, 나무 이름은 20자를 넘을 수 없습니다.)\n" +
                    "[T002]기본 나무를 생성,변경할 수 없습니다. - fruitType에 Default를 지정할경우\n" +
                    "[T003]나무는 최대 4개까지 추가 가능합니다. - 현재 추가한 나무가 4개일때 더 생성하려고 할 경우"),

            @ApiResponse(code = 404, message = "[U005]회원을 찾을 수 없습니다."),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/forest")
    public ResponseEntity<Long> createTree(
            @ApiIgnore @LoginUser LoginUserDto loginUser,
            @Valid @RequestBody TreeRequestDto treeRequestDto) {

        log.info("[나무 추가] userId: {}, request: {}", loginUser.getId(), treeRequestDto);

        if (FruitType.DEFAULT == treeRequestDto.getFruitType()) {
            throw new BetreeException(ErrorCode.TREE_DEFAULT_ERROR, "기본 나무 이외의 다른 나무를 선택해주세요.");
        }

        Long treeId = folderService.createTree(loginUser.getId(), treeRequestDto);
        log.info("[나무 추가 완료] treeId : {}", treeId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(treeId);
    }

    /**
     * 유저 나무 편집
     *
     * @param treeId         편집할 나무 Id
     * @param treeRequestDto 나무(이름,타입) DTO
     * @return
     */
    @ApiOperation(value = "유저 나무 편집", notes = "유저 나무 편집")
    @ApiResponses({
            @ApiResponse(code = 400, message = "[C004]잘못된 ENUM값 입니다.\n" +
                    "[C001]Invalid input value (나무 이름은 빈 값일 수 없습니다, 나무 이름은 20자를 넘을 수 없습니다.)\n" +
                    "[T002]기본 나무를 생성,변경할 수 없습니다. - treeId에 해당하는 나무가 DEFAULT일 경우, requestDto.fruitType이 DEFAULT일 경우"),
            @ApiResponse(code = 403, message = "[U006]잘못된 접근입니다. 유저와 나무 주인이 일치하지 않습니다."),
            @ApiResponse(code = 404, message = "[U005]회원을 찾을 수 없습니다.\n" +
                    "[T001]나무가 존재하지 않습니다."),
    })
    @PutMapping("/api/forest/{treeId}")
    public ResponseEntity<Long> updateTree(
            @ApiIgnore @LoginUser LoginUserDto loginUser,
            @PathVariable Long treeId,
            @Valid @RequestBody TreeRequestDto treeRequestDto) {

        log.info("[나무 편집] userId: {}, treeId: {}, request: {}", loginUser.getId(), treeId, treeRequestDto);

        folderService.updateTree(loginUser.getId(), treeId, treeRequestDto);
        log.info("[나무 편집 완료] treeId : {}, {}", treeId, treeRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(treeId);
    }

    /**
     * 유저 나무 삭제
     *
     * @param loginUser
     * @param treeId
     * @return
     */
    @ApiOperation(value = "유저 나무 삭제", notes = "유저 나무 삭제 - 나무에 포함된 메시지도 모두 삭제")
    @ApiResponses({
            @ApiResponse(code = 400, message = "[T002]기본 나무를 생성,변경할 수 없습니다."),
            @ApiResponse(code = 403, message = "[U006]잘못된 접근입니다. 유저와 나무 주인이 일치하지 않습니다."),
            @ApiResponse(code = 404, message = "[U005]회원을 찾을 수 없습니다.\n" +
                    "[T001]나무가 존재하지 않습니다.")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/forest/{treeId}")
    public ResponseEntity<Void> deleteTree(
            @ApiIgnore @LoginUser LoginUserDto loginUser,
            @PathVariable Long treeId) {
        log.info("[나무 삭제] userId :{}, treeId :{}", loginUser.getId(), treeId);
        folderService.deleteTree(loginUser.getId(), treeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 유저 나무 공개 설정
     *
     * @param loginUser
     * @param treeId
     * @return
     */
    @ApiOperation(value = "유저 나무 공개 설정", notes = "유저 나무 공개 설정 - 공개/비공개")
    @ApiResponses({
            @ApiResponse(code = 400, message = "[T002]기본 나무를 생성,변경할 수 없습니다."),
            @ApiResponse(code = 403, message = "[U006]잘못된 접근입니다. 유저와 나무 주인이 일치하지 않습니다."),
            @ApiResponse(code = 404, message = "[U005]회원을 찾을 수 없습니다.\n" +
                    "[T001]나무가 존재하지 않습니다.")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/api/forest/opening")
    public ResponseEntity<Void> openTree(@ApiIgnore @LoginUser LoginUserDto loginUser,
                                         @RequestParam Long treeId) {

        log.info("[나무 공개 설정] userId :{}, treeId :{}", loginUser.getId(), treeId);
        folderService.updateTreeOpening(loginUser.getId(), treeId);
        return ResponseEntity.noContent().build();
    }
}