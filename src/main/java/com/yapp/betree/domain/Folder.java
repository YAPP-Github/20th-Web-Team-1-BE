package com.yapp.betree.domain;

import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "folders")
public class Folder extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private FruitType fruit;

    @Column(nullable = false)
    private Long level;

    private boolean opening;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Folder(Long id, String name, FruitType fruit, Long level, boolean opening, User user) {
        this.id = id;
        this.name = name;
        this.fruit = fruit;
        this.level = level;
        this.opening = opening;
        this.user = user;
    }

    public boolean isDefault() {
        return this.fruit == FruitType.DEFAULT;
    }

    public void updateUser(User user) {
        this.user = user;
    }

    /**
     * 나무 편집 메서드
     */
    public void update(String name, FruitType fruit) {
        if (fruit == FruitType.DEFAULT) {
            throw new BetreeException(ErrorCode.TREE_DEFAULT_ERROR, "변경할 타입을 기본 나무 이외의 다른 나무로 선택해주세요. treeId = " + id + ", FruitType = " + fruit);
        }
        this.name = name;
        this.fruit = fruit;
    }

    /**
     * 공개 여부 상태 변경 메서드
     */
    public void updateOpening() {
        this.opening = !this.opening;
    }
}
