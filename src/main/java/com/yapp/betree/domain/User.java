package com.yapp.betree.domain;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private Long oauthId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String userImage;

    @Column(nullable = false)
    private LocalDateTime lastAccessTime;

    @Column(nullable = false)
    private String url;

    private boolean randomMessageRemind;
    private boolean forestOff;
    private boolean onlyFriends;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Message> receivedMessages = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private List<Folder> folders = new ArrayList<>();

    @Builder
    public User(Long id, Long oauthId, String nickname, String email, String userImage, LocalDateTime lastAccessTime, String url, boolean randomMessageRemind, boolean forestOff, boolean onlyFriends, List<Message> receivedMessages, List<Folder> folders) {
        this.id = id;
        this.oauthId = oauthId;
        this.nickname = nickname;
        this.email = email;
        this.userImage = userImage;
        this.lastAccessTime = lastAccessTime;
        this.url = url;
        this.randomMessageRemind = randomMessageRemind;
        this.forestOff = forestOff;
        this.onlyFriends = onlyFriends;
        this.receivedMessages = receivedMessages;
        if (Objects.isNull(receivedMessages)) {
            this.receivedMessages = new ArrayList<>();
        }
        this.folders = folders;
        if (Objects.isNull(folders)) {
            this.folders = new ArrayList<>();
        }
    }

    public void addFolder(Folder folder) {
        this.folders.add(folder);
        folder.updateUser(this); // 원래 이거만 해도 추가됨
    }

    /**
     * 유저 닉네임 변경 메소드
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
