package com.yapp.betree.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "messages")
public class Message extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(length = 1000, nullable = false)
    private String content;

    @Column(nullable = false)
    private Long senderId;

    private boolean anonymous;
    private boolean alreadyRead;
    private boolean favorite;
    private boolean opening;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @Builder
    public Message(Long id, String content, Long senderId, boolean anonymous, boolean alreadyRead, boolean favorite, boolean opening, User user, Folder folder) {
        this.id = id;
        this.content = content;
        this.senderId = senderId;
        this.anonymous = anonymous;
        this.alreadyRead = alreadyRead;
        this.favorite = favorite;
        this.opening = opening;
        this.user = user;
        this.folder = folder;
    }

    /**
     * 읽음 여부 상태 변경 메서드
     */
    public void updateAlreadyRead() {
        this.alreadyRead = !this.alreadyRead;
    }

    /**
     * 익명 여부 상태 변경 메서드
     */
    public void updateAnonymous() {
        this.anonymous = !this.anonymous;
    }
}
