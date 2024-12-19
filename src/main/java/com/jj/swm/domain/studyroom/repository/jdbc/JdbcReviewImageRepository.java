package com.jj.swm.domain.studyroom.repository.jdbc;

import com.jj.swm.domain.studyroom.entity.StudyRoomReview;

import java.util.List;

public interface JdbcReviewImageRepository {
    void batchInsert(List<String> imageUrls, StudyRoomReview studyRoomReview);
}
