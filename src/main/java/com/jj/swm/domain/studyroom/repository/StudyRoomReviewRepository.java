package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudyRoomReviewRepository extends JpaRepository<StudyRoomReview, Long> {

    int countStudyRoomReviewByStudyRoom(StudyRoom studyRoom);

    Optional<StudyRoomReview> findByIdAndUserId(Long studyRoomReviewId, UUID userId);

    @Query(value = "select srr.* " +
            "from study_room_review srr " +
            "inner join study_room sr on srr.study_room_id = sr.id " +
            "inner join users u on sr.user_id = u.id " +
            "where srr.id = :studyRoomReviewId and (srr.user_id = :userId or u.id = :userId )",
            nativeQuery = true)
    Optional<StudyRoomReview> findByStudyRoomReviewWithNativeQuery(
            @Param("studyRoomReviewId") Long studyRoomReviewId,
            @Param("userId") UUID userId);

}
