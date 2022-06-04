package com.yapp.betree.domain;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String nickName;

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

    @Builder
    public User(Long id, Long oauthId, String nickName, String email, String userImage, LocalDateTime lastAccessTime, String url, boolean randomMessageRemind, boolean forestOff, boolean onlyFriends, List<Message> receivedMessages) {
        this.id = id;
        this.oauthId = oauthId;
        this.nickName = nickName;
        this.email = email;
        this.userImage = userImage;
        this.lastAccessTime = lastAccessTime;
        this.url = url;
        this.randomMessageRemind = randomMessageRemind;
        this.forestOff = forestOff;
        this.onlyFriends = onlyFriends;
        this.receivedMessages = receivedMessages;
    }
}
