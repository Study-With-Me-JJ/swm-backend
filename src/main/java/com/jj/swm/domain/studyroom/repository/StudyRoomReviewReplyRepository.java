package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoomReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudyRoomReviewReplyRepository extends JpaRepository<StudyRoomReviewReply, Long> {

    Optional<StudyRoomReviewReply> findByIdAndUserId(Long studyRoomReviewReplyId, UUID userId);

    @Modifying
    @Query("update StudyRoomReviewReply s set s.deletedAt = CURRENT_TIMESTAMP where s.studyRoomReview.id = ?1")
    void deleteAllByStudyRoomReviewId(Long studyRoomReviewId);

    @Modifying
    @Query("update StudyRoomReviewReply s set s.deletedAt = CURRENT_TIMESTAMP where s.studyRoomReview.id in (?1)")
    void deleteAllByStudyRoomReviewIdIn(List<Long> reviewIds);
}
