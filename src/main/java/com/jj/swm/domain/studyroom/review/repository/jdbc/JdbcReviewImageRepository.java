package com.jj.swm.domain.studyroom.review.repository.jdbc;

import com.jj.swm.domain.studyroom.review.entity.StudyRoomReview;

import java.util.List;

public interface JdbcReviewImageRepository {
    void batchInsert(List<String> imageUrls, StudyRoomReview studyRoomReview);
}
