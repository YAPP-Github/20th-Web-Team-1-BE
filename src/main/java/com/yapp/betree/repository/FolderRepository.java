package com.yapp.betree.repository;

import com.yapp.betree.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    Folder findByUserIdAndName(Long userId, String name);
}
