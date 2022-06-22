package com.yapp.betree.controller;

import com.yapp.betree.annotation.LoginUser;
import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.dto.request.TreeRequestDto;
import com.yapp.betree.dto.response.ForestResponseDto;
import com.yapp.betree.dto.response.TreeFullResponseDto;
import com.yapp.betree.service.FolderService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@RequiredArgsConstructor
@Slf4j
public class ForestController {

    private final FolderService folderService;

    /**
     * 유저 나무숲 조회
     *
     * @param loginUser
     * @return ForestResponseDto
     */
    @GetMapping("/api/forest")
    public ResponseEntity<ForestResponseDto> userForest(@ApiIgnore @LoginUser LoginUserDto loginUser) {

        log.info("나무숲 조회 userId: {}", loginUser.getId());
        return ResponseEntity.ok(folderService.userForest(loginUser.getId()));
    }

    /**
     * 유저 상세 나무 조회
     *
     * @param userId
     * @param treeId
     * @return TreeFullResponseDto
     */
    @GetMapping("/api/forest/{treeId}")
    public ResponseEntity<TreeFullResponseDto> userDetailTree(
            @RequestParam Long userId,
            @PathVariable Long treeId) throws Exception {

        log.info("유저 상세 나무 조회 userId: {}", userId);
        return ResponseEntity.ok(folderService.userDetailTree(userId, treeId));
    }

    /**
     * 유저 나무 추가
     *
     * @param userId
     * @param treeRequestDto 나무(이름,타입) DTO
     * @return
     */
    @PostMapping("/api/forest")
    public ResponseEntity<Object> createTree(
            @RequestParam Long userId,
            @RequestBody TreeRequestDto treeRequestDto) throws Exception {

        log.info("나무 추가 userId: {}", userId);

        folderService.createTree(userId, treeRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 유저 나무 편집
     *
     * @param userId
     * @param treeId         편집할 나무 Id
     * @param treeRequestDto 나무(이름,타입) DTO
     * @return
     */
    @PutMapping("/api/forest/{treeId}")
    public ResponseEntity<Object> updateTree(
            @RequestParam Long userId,
            @PathVariable Long treeId,
            @RequestBody TreeRequestDto treeRequestDto) throws Exception {

        log.info("나무 편집 treeId: {}", treeId);

        folderService.updateTree(userId, treeId, treeRequestDto);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
