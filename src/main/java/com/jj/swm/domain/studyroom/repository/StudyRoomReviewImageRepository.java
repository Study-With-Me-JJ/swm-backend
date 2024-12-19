package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoomReviewImage;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcReviewImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRoomReviewImageRepository extends JpaRepository<StudyRoomReviewImage, Long>, JdbcReviewImageRepository {

    void deleteAllByStudyRoomReviewId(Long studyRoomReviewId);
}
