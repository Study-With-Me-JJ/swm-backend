package com.jj.swm.domain.study.comment.fixture.request;

import com.jj.swm.domain.study.comment.dto.request.UpsertCommentRequest;

public class CommentRequestFixture {

    public static UpsertCommentRequest buildUpsertCommentRequest() {
        return UpsertCommentRequest.builder()
                .content("test_content")
                .build();
    }
}
