package com.jj.swm.domain.study.comment.fixture.dto.request;

import com.jj.swm.domain.study.comment.dto.request.UpsertStudyCommentRequest;

public class UpsertStudyCommentRequestFixture {

    public static UpsertStudyCommentRequest create() {
        return UpsertStudyCommentRequest.builder()
                .content("test_content")
                .build();
    }

    public static UpsertStudyCommentRequest update() {
        return UpsertStudyCommentRequest.builder()
                .content("updated_content")
                .build();
    }
}
