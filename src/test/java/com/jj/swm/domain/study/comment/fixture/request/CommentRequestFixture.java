package com.jj.swm.domain.study.comment.fixture.request;

import com.jj.swm.domain.study.comment.dto.request.UpsertCommentRequest;

public class CommentRequestFixture {

    public static UpsertCommentRequest buildCreateCommentRequest() {
        return UpsertCommentRequest.builder()
                .content("test_content")
                .build();
    }

    public static UpsertCommentRequest buildUpdateCommentRequest() {
        return UpsertCommentRequest.builder()
                .content("updated_content")
                .build();
    }
}
