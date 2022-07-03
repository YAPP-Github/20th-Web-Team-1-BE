package com.yapp.betree.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "칭찬 메시지를 보낼 나무숲 주인 유저 아이디(받는사람)")
    private Long receiverId;

    @NotBlank(message = "메시지를 입력해주세요.")
    @Length(min = 10, max = 1000, message = "메시지는 최소 10자, 최대 1000자 입력해야합니다.")
    private String content;

    @Positive(message = "올바른 id를 입력해주세요.")
    @ApiModelProperty(value = "칭찬 메시지를 보낼 지정 폴더, 자신에게 보낼때만 id를 명시하고 다른사람에게 보낼 때는 null로 입력")
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
