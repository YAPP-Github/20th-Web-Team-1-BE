package com.yapp.betree.service;


import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.request.TreeRequestDto;
import com.yapp.betree.dto.response.MessageResponseDto;
import com.yapp.betree.dto.response.TreeFullResponseDto;
import com.yapp.betree.dto.response.TreeResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FolderService {

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final MessageRepository messageRepository;


    /**
     * 유저 나무숲 조회
     *
     * @param userId
     * @return List<TreeResponseDto>
     */
    public List<TreeResponseDto> userForest(Long userId) {
        return folderRepository.findAllByUserId(userId)
                .stream()
                .map(TreeResponseDto::of)
                .collect(Collectors.toList());
    }

    /**
     * 유저 상세 나무 조회
     *
     * @param userId
     * @param treeId
     * @return TreeFullResponseDto
     */
    public TreeFullResponseDto userDetailTree(Long userId, Long treeId) {

        Folder folder = folderRepository.findById(treeId).orElseThrow(() -> new BetreeException(ErrorCode.TREE_NOT_FOUND, "treeId = " + treeId));

        if (folder.getUser().getId() != userId) {
            throw new BetreeException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 접근입니다. 유저와 나무의 주인이 일치하지 않습니다.");
        }

        // 이전, 다음 폴더 없을때 0L으로 처리
        Long prevId = folderRepository.findTop1ByUserAndIdLessThanOrderByIdDesc(folder.getUser(), treeId)
                .map(Folder::getId)
                .orElse(0L);
        Long nextId = folderRepository.findTop1ByUserAndIdGreaterThan(folder.getUser(), treeId)
                .map(Folder::getId)
                .orElse(0L);

        // opening == true 인 메세지 8개 가져오기
        List<MessageResponseDto> messageResponseDtos = messageRepository.findTop8ByFolderIdAndOpening(treeId, true)
                .stream()
                .map(m -> {
                    User sender = userRepository.findById(m.getSenderId()).orElseThrow(() -> new BetreeException(ErrorCode.USER_NOT_FOUND, "userID = " + m.getSenderId()));
                    return MessageResponseDto.of(m, sender);
                })
                .collect(Collectors.toList());
        return new TreeFullResponseDto(folder, prevId, nextId, messageResponseDtos);
    }

    /**
     * 나무(폴더) 생성
     *
     * @param userId
     * @param treeRequestDto
     */
    @Transactional
    public Long createTree(Long userId, TreeRequestDto treeRequestDto) {

        User user = userRepository.findById(userId).orElseThrow(() -> new BetreeException(ErrorCode.USER_NOT_FOUND, "userID = " + userId));

        Folder folder = Folder.builder()
                .fruit(treeRequestDto.getFruitType())
                .user(user)
                .name(treeRequestDto.getName())
                .level(0L)
                .build();

        return folderRepository.save(folder).getId();
    }

    /**
     * 나무(폴더) 편집
     *
     * @param userId
     * @param treeId
     * @param treeRequestDto
     */
    @Transactional
    public void updateTree(Long userId, Long treeId, TreeRequestDto treeRequestDto) throws Exception {

        Folder folder = folderRepository.findById(treeId).orElseThrow(Exception::new);
        folder.update(treeRequestDto.getName(), treeRequestDto.getFruitType());
    }
}
