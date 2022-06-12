package com.yapp.betree.repository;

import com.yapp.betree.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByUserId(Long userId);

    Folder findByUserIdAndId(Long userId, Long treeId);
}
