package com.yapp.betree.repository;

import com.yapp.betree.domain.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUserIdAndOpening(Long userId, boolean opening);

    Slice<Message> findByUserId(Long userId, Pageable pageable);
}
