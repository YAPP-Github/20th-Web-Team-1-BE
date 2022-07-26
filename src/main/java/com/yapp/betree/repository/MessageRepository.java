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

    // 특정 Folder, user에 해당하는 전체 메시지 조회
    List<Message> findAllByUserIdAndFolderIdAndDelByReceiver(Long userId, Long folderId, boolean delByReceiver);

    /* 폴더별 메세지 상세조회시 사용 */
    //prev
    Optional<Message> findTop1ByUserIdAndFolderIdAndDelByReceiverAndIdLessThanOrderByIdDesc(Long userId, Long folderId, boolean delByReceiver, Long messageId);

    //next
    Optional<Message> findTop1ByUserIdAndFolderIdAndDelByReceiverAndIdGreaterThan(Long userId, Long folderId, boolean delByReceiver, Long messageId);

    /* 즐겨찾기 메세지 상세조회시 사용 */
    //prev
    Optional<Message> findTop1ByUserIdAndFavoriteAndDelByReceiverAndIdLessThanOrderByIdDesc(Long userId, boolean favorite, boolean delByReceiver, Long messageId);

    //next
    Optional<Message> findTop1ByUserIdAndFavoriteAndDelByReceiverAndIdGreaterThan(Long userId, boolean favorite, boolean delByReceiver, Long messageId);
}
