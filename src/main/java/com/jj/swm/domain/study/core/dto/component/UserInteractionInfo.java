package com.jj.swm.domain.study.core.dto.component;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "좋아요, 북마크를 했으면 각각에 대해 ID값 제공")
public class UserInteractionInfo {

    private Long bookmarkId;

    private Long likeId;

    public static UserInteractionInfo of(Long bookmarkId, Long likeId) {
        return UserInteractionInfo.builder()
                .bookmarkId(bookmarkId)
                .likeId(likeId)
                .build();
    }

    public static UserInteractionInfo empty() {
        return UserInteractionInfo.builder()
                .bookmarkId(null)
                .likeId(null)
                .build();
    }
}
