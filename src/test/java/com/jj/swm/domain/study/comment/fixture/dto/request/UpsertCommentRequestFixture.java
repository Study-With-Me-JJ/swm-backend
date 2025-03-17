package com.jj.swm.domain.study.comment.fixture.dto.request;

import com.jj.swm.domain.study.comment.dto.request.UpsertCommentRequest;

public class UpsertCommentRequestFixture {

    public static UpsertCommentRequest create() {
        return UpsertCommentRequest.builder()
                .content("test_content")
                .build();
    }

    public static UpsertCommentRequest update() {
        return UpsertCommentRequest.builder()
                .content("updated_content")
                .build();
    }
}
