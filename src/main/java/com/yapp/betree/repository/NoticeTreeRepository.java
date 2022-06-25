package com.yapp.betree.repository;

import com.yapp.betree.domain.NoticeTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeTreeRepository extends JpaRepository<NoticeTree, Long> {
    Optional<NoticeTree> findByUserId(Long userId);
}
