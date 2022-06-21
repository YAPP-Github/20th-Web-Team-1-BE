package com.yapp.betree.service;


import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.Message;
import com.yapp.betree.domain.User;
import com.yapp.betree.dto.request.TreeRequestDto;
import com.yapp.betree.dto.response.ForestResponseDto;
import com.yapp.betree.dto.response.MessageResponseDto;
import com.yapp.betree.dto.response.TreeFullResponseDto;
import com.yapp.betree.dto.response.TreeResponseDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import com.yapp.betree.repository.FolderRepository;
import com.yapp.betree.repository.MessageRepository;
import com.yapp.betree.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FolderService {

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final MessageRepository messageRepository;

    private final int PAGE_SIZE = 4;

    /**
     * 유저 나무숲 조회
     *
     * @param userId
     * @return ForestResponseDto
     */
    public ForestResponseDto userForest(Long userId, int page) {
        // TODO - page != 0,1 일때 예외처리 개선
        // 다음 페이지 존재여부 + 빈값 프론트에서 처리하게 할지 ,백엔드에서 에러로 페이지 에러를 보내주는게 나을지 결정 필요
        if (page != 0 && page != 1) {
            throw new BetreeException(ErrorCode.FOREST_PAGE_ERROR, "page = " + page);
        }

        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);

        Slice<Folder> folderList = folderRepository.findByUserId(userId, pageRequest);

        if (!folderList.hasContent()) {
            throw new BetreeException(ErrorCode.FOREST_EMPTY_PAGE, "page = " + page);
        }

        List<TreeResponseDto> treeResponseDtoList = new ArrayList<>();
        for (Folder folder : folderList) {
            treeResponseDtoList.add(new TreeResponseDto(folder.getId(), folder.getName()));
        }

        return new ForestResponseDto(folderList.hasNext(), treeResponseDtoList);
    }

    /**
     * 유저 상세 나무 조회
     *
     * @param userId
     * @param treeId
     * @return TreeFullResponseDto
     */
    public TreeFullResponseDto userDetailTree(Long userId, Long treeId) throws Exception {

        Long prevId;
        Long nextId;

        Folder folder = folderRepository.findById(treeId).orElseThrow(() -> new BetreeException(ErrorCode.TREE_NOT_FOUND, "treeId = " + treeId));

        // 이전, 다음 폴더 없을때 0L으로 처리
        try {
            prevId = folderRepository.findTop1ByUserAndIdLessThanOrderByIdDesc(folder.getUser(), treeId).getId();
        } catch (NullPointerException e) {
            prevId = 0L;
        }

        try {
            nextId = folderRepository.findTop1ByUserAndIdGreaterThan(folder.getUser(), treeId).getId();
        } catch (NullPointerException e) {
            nextId = 0L;
        }

        //opening== true 인 메세지 8개 가져오기
        List<Message> messageList = messageRepository.findTop8ByFolderIdAndOpening(treeId, true);

        //messageList를 dto로 감싸기
        List<MessageResponseDto> messageResponseDtoList = new ArrayList<>();
        for (Message m : messageList) {

            User sender = userRepository.findById(m.getSenderId()).orElseThrow(() -> new BetreeException(ErrorCode.USER_NOT_FOUND, "userID = " + m.getSenderId()));

            //익명이면 닉네임 '익명' 으로 변경
            if (m.isAnonymous()) {
                messageResponseDtoList.add(new MessageResponseDto(m, "익명", "기본 이미지"));
            } else {
                messageResponseDtoList.add(new MessageResponseDto(m, sender.getNickName(), sender.getUserImage()));
            }
        }

        return new TreeFullResponseDto(folder, prevId, nextId, messageResponseDtoList);
    }

    /**
     * 나무(폴더) 생성
     *
     * @param userId
     * @param treeRequestDto
     */
    @Transactional
    public void createTree(Long userId, TreeRequestDto treeRequestDto) throws Exception {

        User user = userRepository.findById(userId).orElseThrow(() -> new BetreeException(ErrorCode.USER_NOT_FOUND, "userID = " + userId));

        Folder folder = Folder.builder()
                .fruit(treeRequestDto.getFruitType())
                .user(user)
                .name(treeRequestDto.getName())
                .level(0L)
                .build();

        folderRepository.save(folder);
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
