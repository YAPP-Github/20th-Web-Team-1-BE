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
    List<Message> findByUserIdAndOpeningAndDelByReceiver(Long userId, boolean opening, boolean delByReceiver);

    Slice<Message> findByUserIdAndFolderIdAndDelByReceiver(Long userId, Long treeId, boolean delByReceiver, Pageable pageable);

    List<Message> findTop8ByFolderIdAndOpeningAndDelByReceiver(Long treeId, boolean opening, boolean delByReceiver);

    List<Message> findByUserIdAndAlreadyReadAndDelByReceiver(Long userId, boolean alreadyRead, boolean delByReceiver);

    // 알림나무 즐겨찾기 메시지 조회용
    List<Message> findAllByUserIdAndFavoriteAndDelByReceiver(Long userId, boolean favorite, boolean delByReceiver);

    Optional<Message> findByIdAndUserIdAndDelByReceiver(Long id, Long userId, boolean delByReceiver);

    // 메시지함 즐겨찾기한 메시지 조회용
    Slice<Message> findByUserIdAndFavoriteAndDelByReceiver(Long userId, boolean favorite, boolean delByReceiver, Pageable pageable);

    //prev
    Optional<Message> findTop1ByUserIdAndIdLessThanOrderByIdDesc(Long userId, Long messageId);

    //next
    Optional<Message> findTop1ByUserIdAndIdGreaterThan(Long userId, Long messageId);
}
