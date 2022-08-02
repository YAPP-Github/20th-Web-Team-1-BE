package com.yapp.betree.service;


import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.SendUserDto;
import com.yapp.betree.dto.request.TreeRequestDto;
import com.yapp.betree.dto.response.ForestResponseDto;
import com.yapp.betree.dto.response.MessageResponseDto;
import com.yapp.betree.dto.response.TreeFullResponseDto;
import com.yapp.betree.dto.response.TreeResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FolderService {

    private final UserService userService;
    private final FolderRepository folderRepository;
    private final MessageRepository messageRepository;


    /**
     * 유저 나무숲 조회
     *
     * @param userId
     * @return ForestResponseDto
     */
    public ForestResponseDto userForest(Long longinUserId, Long userId) {

        User user = userService.findById(userId).orElseThrow(() -> new BetreeException(ErrorCode.USER_NOT_FOUND, "userId = " + userId));

        List<TreeResponseDto> dtoList;

        //로그인유저 == userId라면 전체 다 보여주기, 기본나무는 제외
        if (longinUserId.equals(userId)) {
            dtoList = folderRepository.findAllByUserId(userId)
                    .stream()
                    .filter(folder -> !folder.isDefault())
                    .map(TreeResponseDto::of)
                    .collect(Collectors.toList());
        } else {
            dtoList = folderRepository.findAllByUserId(userId)
                    .stream()
                    .filter(Folder::isOpening)
                    .map(TreeResponseDto::of)
                    .collect(Collectors.toList());
        }
        return ForestResponseDto.builder()
                .nickname(user.getNickname())
                .responseDtoList(dtoList)
                .build();
    }

    /**
     * 유저 상세 나무 조회
     *
     * @param userId
     * @param treeId
     * @return TreeFullResponseDto
     */
    public TreeFullResponseDto userDetailTree(Long userId, Long treeId, Long loginUserId) {

        Folder folder = folderRepository.findById(treeId).orElseThrow(() -> new BetreeException(ErrorCode.TREE_NOT_FOUND, "treeId = " + treeId));

        if (!Objects.equals(folder.getUser().getId(), userId)) {
            throw new BetreeException(ErrorCode.USER_FORBIDDEN, "유저와 나무의 주인이 일치하지 않습니다.");
        }

        if (!folder.isOpening() && !Objects.equals(userId, loginUserId)) {
            throw new BetreeException(ErrorCode.TREE_NOT_FOUND, "treeId = " + treeId);
        }

        // 이전, 다음 폴더 없을때 0L으로 처리
        Long prevId = folderRepository.findTop1ByUserAndFruitIsNotAndIdLessThanOrderByIdDesc(folder.getUser(), FruitType.DEFAULT, treeId)
                .map(Folder::getId)
                .orElse(0L);
        Long nextId = folderRepository.findTop1ByUserAndFruitIsNotAndIdGreaterThan(folder.getUser(), FruitType.DEFAULT, treeId)
                .map(Folder::getId)
                .orElse(0L);

        // opening == true 인 메세지 8개 가져오기
        List<MessageResponseDto> messageResponseDtos = messageRepository.findTop8ByFolderIdAndOpeningAndDelByReceiver(treeId, true, false)
                .stream()
                .map(m -> {
                    SendUserDto sender = userService.findBySenderId(m.getSenderId());
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

        Long count = folderRepository.countByUserIdAndFruitIsNot(userId, FruitType.DEFAULT);
        if (count == 4L) {
            throw new BetreeException(ErrorCode.TREE_COUNT_ERROR, "나무를 추가할 수 없습니다.");
        }

        User user = userService.findById(userId).orElseThrow(() -> new BetreeException(ErrorCode.USER_NOT_FOUND, "userID = " + userId));

        Folder folder = Folder.builder()
                .fruit(treeRequestDto.getFruitType())
                .user(user)
                .name(treeRequestDto.getName())
                .level(0L)
                .opening(true)
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
    public void updateTree(Long userId, Long treeId, TreeRequestDto treeRequestDto) {
        Folder folder = validateAndGetFolder(userId, treeId);

        folder.update(treeRequestDto.getName(), treeRequestDto.getFruitType());
    }

    @Transactional
    public void deleteTree(Long userId, Long treeId) {
        Folder folder = validateAndGetFolder(userId, treeId);

        Folder defaultFolder = folderRepository.findByUserIdAndFruit(userId, FruitType.DEFAULT);
        List<Message> messages = messageRepository.findAllByUserIdAndFolderIdAndDelByReceiver(userId, folder.getId(), false);
        log.info("[폴더 삭제] 폴더에 포함된 메시지 전부 삭제처리 및 폴더 기본폴더로 지정 messages = {}", messages);
        messages.stream()
                .forEach(message -> message.updateDeleteStatus(userId, defaultFolder));

        log.info("[폴더 삭제] folderId = {}", folder.getId());
        folderRepository.delete(folder);
    }

    /**
     * 유저 나무 공개 설정
     *
     * @param userId
     * @param treeId
     */
    @Transactional
    public void updateTreeOpening(Long userId, Long treeId) {
        Folder folder = validateAndGetFolder(userId, treeId);
        folder.updateOpening();
    }

    /**
     * 나무(폴더) 처리시에 userId, treeId 검증 및 tree 주인과 user 일치여부 파악
     * 기본나무는 수정,삭제 불가능하므로 검증
     *
     * @param userId
     * @param treeId
     * @return Folder
     */
    private Folder validateAndGetFolder(Long userId, Long treeId) {
        userService.findById(userId).orElseThrow(() -> new BetreeException(ErrorCode.USER_NOT_FOUND, "userId = " + userId));
        Folder folder = folderRepository.findById(treeId).orElseThrow(() -> new BetreeException(ErrorCode.TREE_NOT_FOUND, "treeId = " + treeId));

        if (!folder.getUser().getId().equals(userId)) {
            throw new BetreeException(ErrorCode.USER_FORBIDDEN, "유저와 나무의 주인이 일치하지 않습니다.");
        }

        if (folder.isDefault()) {
            throw new BetreeException(ErrorCode.TREE_DEFAULT_ERROR, "treeId = " + treeId);
        }
        return folder;
    }
}
