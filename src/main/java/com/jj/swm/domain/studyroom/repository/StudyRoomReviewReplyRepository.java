package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoomReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudyRoomReviewReplyRepository extends JpaRepository<StudyRoomReviewReply, Long> {

    Optional<StudyRoomReviewReply> findByIdAndUserId(Long studyRoomReviewReplyId, UUID userId);
}
