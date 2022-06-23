package com.yapp.betree.repository;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findAllByUserId(Long userId);

    //prev
    Folder findTop1ByUserAndIdLessThanOrderByIdDesc(User user, Long id);

    //next
    Folder findTop1ByUserAndIdGreaterThan(User user, Long id);
}
