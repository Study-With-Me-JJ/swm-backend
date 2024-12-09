package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomBookmark;
import com.jj.swm.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudyRoomBookmarkRepository extends JpaRepository<StudyRoomBookmark, Long> {

    boolean existsByStudyRoomIdAndUserId(Long studyRoomId, UUID userId);

    @Query("select s from StudyRoomBookmark s join fetch s.user where s.id = ?1")
    Optional<StudyRoomBookmark> findByIdWithUser(Long studyRoomBookmarkId);
}
