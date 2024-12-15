package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoomBookmark;
import com.jj.swm.domain.studyroom.repository.custom.CustomStudyRoomBookmarkRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudyRoomBookmarkRepository extends JpaRepository<StudyRoomBookmark, Long>, CustomStudyRoomBookmarkRepository {

    boolean existsByStudyRoomIdAndUserId(Long studyRoomId, UUID userId);

    Optional<StudyRoomBookmark> findByIdAndUserId(Long studyRoomBookmarkId, UUID userId);
}
