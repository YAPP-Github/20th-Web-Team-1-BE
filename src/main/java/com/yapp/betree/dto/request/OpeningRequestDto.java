package com.yapp.betree.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(description = "열매맺기 요청 DTO")
public class OpeningRequestDto {

    @NotNull(message = "열매 맺을 메세지 아이디를 입력해주세요.")
    private List<Long> messageIds;

    @NotNull(message = "열매 맺을 나무 아이디를 입력해주세요.")
    private Long treeId;

    @Builder
    public OpeningRequestDto(List<Long> messageIds, Long treeId) {
        this.messageIds = messageIds;
        this.treeId = treeId;
    }
}
