package com.yapp.betree.controller;

import com.yapp.betree.dto.response.ForestResponseDto;
import com.yapp.betree.service.FolderService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequiredArgsConstructor
@Slf4j
public class ForestController {

    private FolderService folderService;

    @GetMapping("/api/users/{userId}/forest")
    public ResponseEntity<ForestResponseDto> userForest(
            @PathVariable Long userId) {

        log.info("나무숲 조회 userId: {}", userId);
        return ResponseEntity.ok(folderService.userForest(userId));
    }
}
