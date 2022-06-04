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
    private Long sender;

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
    public Message(Long id, String content, Long sender, boolean anonymous, boolean alreadyRead, boolean favorite, boolean opening, User user, Folder folder) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.anonymous = anonymous;
        this.alreadyRead = alreadyRead;
        this.favorite = favorite;
        this.opening = opening;
        this.user = user;
        this.folder = folder;
    }
}
