package com.yapp.betree.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
@Table(name = "notice_trees")
public class NoticeTree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_tree_id")
    private Long id;

    @Column(nullable = false)
    private String unreadMessages;

    @Column
    private String readMessages = "";

    @Column(nullable = false)
    private Long userId;

    @Builder
    public NoticeTree(Long id, String unreadMessages, String readMessages, Long userId) {
        this.id = id;
        this.unreadMessages = unreadMessages;
        this.readMessages = readMessages;
        this.userId = userId;
    }

    public void resetMessages(String unreadMessages) {
        this.unreadMessages = unreadMessages;
        this.readMessages = "";
    }
    public void updateMessages(String unreadMessages, String readMessages) {
        this.unreadMessages = unreadMessages;
        this.readMessages = readMessages;
    }
}
