package com.jj.swm.domain.studyroom.core.repository;

import com.jj.swm.domain.studyroom.core.entity.StudyRoomBookmark;
import com.jj.swm.domain.studyroom.core.repository.custom.CustomStudyRoomBookmarkRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudyRoomBookmarkRepository extends JpaRepository<StudyRoomBookmark, Long>, CustomStudyRoomBookmarkRepository {

    boolean existsByStudyRoomIdAndUserId(Long studyRoomId, UUID userId);

    Optional<StudyRoomBookmark> findByIdAndUserId(Long studyRoomBookmarkId, UUID userId);

    @Modifying
    @Query("delete from StudyRoomBookmark s where s.studyRoom.id = ?1")
    void deleteAllByStudyRoomId(Long studyRoomId);
}
