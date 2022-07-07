package com.yapp.betree.repository;

import com.yapp.betree.domain.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUserIdAndOpening(Long userId, boolean opening);

    Slice<Message> findByUserId(Long userId, Pageable pageable);

    Slice<Message> findByUserIdAndFolderId(Long userId, Long treeId, Pageable pageable);

    List<Message> findTop8ByFolderIdAndOpening(Long treeId, boolean opening);

    List<Message> findByUserIdAndAlreadyRead(Long userId, boolean alreadyRead);

    // 알림나무 즐겨찾기 메시지 조회용
    List<Message> findAllByUserIdAndFavorite(Long userId, boolean favorite);

    Optional<Message> findByIdAndUserId(Long id, Long userId);

    // 메시지함 즐겨찾기한 메시지 조회용
    Slice<Message> findByUserIdAndFavorite(Long userId, boolean favorite, Pageable pageable);

    //prev
    Optional<Message> findTop1ByUserIdAndIdLessThanOrderByIdDesc(Long userId, Long messageId);

    //next
    Optional<Message> findTop1ByUserIdAndIdGreaterThan(Long userId, Long messageId);
}
