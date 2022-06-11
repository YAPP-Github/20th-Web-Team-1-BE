package com.yapp.betree.controller;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.dto.request.TreeRequestDto;
import com.yapp.betree.dto.response.ForestResponseDto;
import com.yapp.betree.dto.response.TreeFullResponseDto;
import com.yapp.betree.service.FolderService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Api
@RestController
@RequiredArgsConstructor
@Slf4j
public class ForestController {

    private final FolderService folderService;

    /**
     * 유저 나무숲 조회
     * @param userId
     * @return ForestResponseDto
     */
    @GetMapping("/api/forest")
    public ResponseEntity<ForestResponseDto> userForest(
            @RequestParam Long userId) {

        log.info("나무숲 조회 userId: {}", userId);
        return ResponseEntity.ok(folderService.userForest(userId));
    }

    /**
     * 유저 상세 나무 조회
     * @param userId
     * @param treeId
     * @return TreeFullResponseDto
     */
    @GetMapping("/api/forest/{treeId}")
    public ResponseEntity<TreeFullResponseDto> userDetailTree(
            @RequestParam Long userId,
            @PathVariable Long treeId) {

        log.info("유저 상세 나무 조회 userId: {}", userId);
        return ResponseEntity.ok(folderService.userDetailTree(userId, treeId));
    }

    /**
     * 유저 나무 추가
     * @param userId
     * @param treeRequestDto 나무(이름,타입) DTO
     * @return
     */
    @PostMapping("/api/trees")
    public ResponseEntity<TreeFullResponseDto> createTree(
            @RequestParam Long userId,
            @RequestBody TreeRequestDto treeRequestDto) throws Exception {

        log.info("나무 추가 userId: {}", userId);
        Folder tree = folderService.createTree(userId, treeRequestDto);

        return ResponseEntity.created(URI.create("/trees/" + tree.getId())).build();
    }

    /**
     * 유저 나무 편집
     * @param userId
     * @param treeId 편집할 나무 Id
     * @param treeRequestDto 나무(이름,타입) DTO
     * @return
     */
    @PutMapping("/api/trees/{treeId}")
    public ResponseEntity<TreeFullResponseDto> updateTree(
            @RequestParam Long userId,
            @PathVariable Long treeId,
            @RequestBody TreeRequestDto treeRequestDto) throws Exception {

        log.info("나무 편집 treeId: {}", treeId);
        Folder tree = folderService.updateTree(userId, treeId, treeRequestDto);

        return ResponseEntity.created(URI.create("/trees/" + tree.getId())).build();
    }
}
