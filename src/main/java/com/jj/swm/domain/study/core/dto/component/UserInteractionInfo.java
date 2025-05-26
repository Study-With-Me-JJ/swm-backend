package com.jj.swm.domain.study.core.dto.component;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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
