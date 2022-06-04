package com.yapp.betree.service;

import com.yapp.betree.dto.response.ForestResponseDto;
import com.yapp.betree.dto.response.TreeFullResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FolderService {

    public ForestResponseDto userForest(Long userId) {
        return null;
    }

    public TreeFullResponseDto userDetailTree(Long userId) {
        return null;
    }
}
