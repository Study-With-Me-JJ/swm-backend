package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoomReviewImage;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcReviewImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRoomReviewImageRepository extends JpaRepository<StudyRoomReviewImage, Long>, JdbcReviewImageRepository {

    @Modifying
    @Query("delete from StudyRoomReviewImage s where s.studyRoomReview.id = ?1")
    void deleteAllByStudyRoomReviewId(Long studyRoomReviewId);

    @Modifying
    @Query("delete from StudyRoomReviewImage s where s.studyRoomReview.id in (?1)")
    void deleteAllByStudyRoomReviewIdIn(List<Long> studyRoomReviewIds);
}
