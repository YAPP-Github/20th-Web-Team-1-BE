package com.yapp.betree.repository;

import com.yapp.betree.domain.Folder;
import com.yapp.betree.domain.FruitType;
import com.yapp.betree.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findAllByUserId(Long userId);

    Folder findByUserIdAndFruit(Long userId, FruitType fruitType);

    //prev
    Optional<Folder> findTop1ByUserAndIdLessThanOrderByIdDesc(User user, Long id);

    //next
    Optional<Folder> findTop1ByUserAndIdGreaterThan(User user, Long id);

    Long countByUserIdAndFruitIsNot(Long userId, FruitType fruitType);
}
