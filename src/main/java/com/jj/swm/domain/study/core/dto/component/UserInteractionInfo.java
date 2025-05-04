package com.jj.swm.domain.study.core.dto.component;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInteractionInfo {

    private Long bookmarkId;

    private boolean liked;

    public static UserInteractionInfo of(Long bookmarkId, boolean liked) {
        return UserInteractionInfo.builder()
                .bookmarkId(bookmarkId)
                .liked(liked)
                .build();
    }
}
