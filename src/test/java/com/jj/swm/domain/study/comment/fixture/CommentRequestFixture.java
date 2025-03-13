package com.jj.swm.domain.study.comment.fixture;

import com.jj.swm.domain.study.comment.dto.request.UpsertCommentRequest;

public class CommentRequestFixture {

    public static UpsertCommentRequest createCommentRequest() {
        return UpsertCommentRequest.builder()
                .content("test_content")
                .build();
    }

    public static UpsertCommentRequest updateCommentRequest() {
        return UpsertCommentRequest.builder()
                .content("updated_content")
                .build();
    }
}
