package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomLike;
import com.jj.swm.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudyRoomLikeRepository extends JpaRepository<StudyRoomLike, Long> {

    boolean existsByStudyRoomIdAndUserId(Long studyRoomId, UUID userId);

    Optional<StudyRoomLike> findByStudyRoomIdAndUserId(Long studyRoomId, UUID userId);

    int countStudyRoomLikeByStudyRoom(StudyRoom studyRoom);
}
