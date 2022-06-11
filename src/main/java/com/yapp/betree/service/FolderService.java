package com.yapp.betree.service;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.request.TreeRequestDto;
import com.yapp.betree.dto.response.ForestResponseDto;
import com.yapp.betree.dto.response.TreeFullResponseDto;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FolderService {

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;

    public ForestResponseDto userForest(Long userId) {
        return null;
    }

    public TreeFullResponseDto userDetailTree(Long userId, Long treeId) {
        return null;
    }

    /**
     * 나무(폴더) 생성
     * @param userId
     * @param treeRequestDto
     */
    @Transactional
    public Folder createTree(Long userId, TreeRequestDto treeRequestDto) throws Exception {

        User user = userRepository.findById(userId).orElseThrow(Exception::new);

        Folder folder = Folder.builder()
                .fruit(treeRequestDto.getFruitType())
                .user(user)
                .name(treeRequestDto.getName())
                .level(0L)
                .build();

        folderRepository.save(folder);

        return folder;
    }

    /**
     * 나무(폴더) 편집
     * @param userId
     * @param treeId
     * @param treeRequestDto
     */
    @Transactional
    public Folder updateTree(Long userId, Long treeId, TreeRequestDto treeRequestDto) throws Exception {

        Folder folder = folderRepository.findById(treeId).orElseThrow(Exception::new);
        folder.update(treeRequestDto.getName(), treeRequestDto.getFruitType());

        return folder;
    }
}
