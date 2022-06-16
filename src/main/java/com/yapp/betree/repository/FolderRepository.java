package com.yapp.betree.repository;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    Slice<Folder> findByUserId(Long userId, Pageable pageable);

    //prev
    Folder findTop1ByUserAndIdLessThanOrderByIdDesc(User user, Long id);

    //next
    Folder findTop1ByUserAndIdGreaterThan(User user, Long id);
}
