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
    private boolean delBySender;
    private boolean delByReceiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;


    @Builder
    public Message(Long id, String content, Long senderId, boolean anonymous, boolean alreadyRead, boolean favorite, boolean opening, boolean delBySender, boolean delByReceiver, User user, Folder folder) {
        this.id = id;
        this.content = content;
        this.senderId = senderId;
        this.anonymous = anonymous;
        this.alreadyRead = alreadyRead;
        this.favorite = favorite;
        this.opening = opening;
        this.delBySender = delBySender;
        this.delByReceiver = delByReceiver;
        this.user = user;
        this.folder = folder;
    }

    public static Message generateWelcomeMessage(User user, Folder folder) {
        return Message.builder()
                .content("STEP 1.\n \"물 주기\"를 통해 칭찬 메시지를 작성하세요!\n" +
                        "STEP 2.\n 다양한 열매 나무를 심고 칭찬 메시지를 분류하세요!\n" +
                        "STEP 3.\n 열매 맺을 메시지를 선택해 나만의 나무숲을 가꾸세요!\n" +
                        "STEP 4.\n URL을 통해 나만의 나무숲을 공유하고 나무를 무럭무럭 키우세요!")
                .senderId(-999L)
                .anonymous(false)
                .alreadyRead(false)
                .favorite(false)
                .opening(true)
                .delBySender(false)
                .delByReceiver(false)
                .user(user)
                .folder(folder)
                .build();
    }

    /**
     * 읽음 여부 상태 변경 메서드
     */
    public void updateAlreadyRead() {
        this.alreadyRead = true; //false 될 수 없음
    }

    /**
     * 익명 여부 상태 변경 메서드
     */
    public void updateAnonymous() {
        this.anonymous = !this.anonymous;
    }

    /**
     * 공개 여부 상태 변경 메서드
     */
    public void updateOpening() {
        this.opening = !this.opening;
    }

    /**
     * 폴더 변경 메서드
     */
    public void updateFolder(Folder folder) {
        this.folder = folder;
    }

    /**
     * 메세지 즐겨찾기 상태 변경 메서드
     */
    public void updateFavorite() {
        this.favorite = !this.favorite;
    }

    /**
     * 메세지 삭제 여부 상태 변경 메서드
     */
    public void updateDeleteStatus(Long userId, Folder defaultFolder) {
        if (userId.equals(this.senderId)) {
            this.delBySender = true;
        }
        if (userId.equals(this.user.getId())) {
            this.delByReceiver = true;
            updateAlreadyRead(); // 안 읽고 삭제할 때
        }
        this.folder = defaultFolder;
    }
}
