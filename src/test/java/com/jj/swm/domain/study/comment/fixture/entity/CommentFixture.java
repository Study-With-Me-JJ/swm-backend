package com.jj.swm.domain.study.comment.fixture.entity;

import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.user.core.entity.User;

public class CommentFixture {

    public static StudyComment buildStudyComment(User user, Study study) {
        return StudyComment.builder()
                .content("test_content")
                .study(study)
                .user(user)
                .build();
    }
}
