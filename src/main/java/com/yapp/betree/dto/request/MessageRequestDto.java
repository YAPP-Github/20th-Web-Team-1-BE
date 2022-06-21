package com.yapp.betree.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(description = "물주기 요청 DTO")
public class MessageRequestDto {

    @NotNull(message = "받는사람 id를 입력해주세요.")
    @Positive(message = "올바른 id를 입력해주세요.")
    private Long receiverId;

    @NotBlank(message = "메시지를 입력해주세요.")
    @Length(min = 10, max = 1000, message = "메시지는 최소 10자, 최대 1000자 입력해야합니다.")
    private String content;

    @NotNull(message = "폴더 id를 입력해주세요.")
    @Positive(message = "올바른 id를 입력해주세요.")
    private Long folderId;

    private boolean anonymous;

    @Builder
    public MessageRequestDto(Long receiverId, String content, Long folderId, boolean anonymous) {
        this.receiverId = receiverId;
        this.content = content;
        this.folderId = folderId;
        this.anonymous = anonymous;
    }
}
