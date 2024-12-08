package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    @Query("select s from StudyRoom s join fetch s.user " +
            "where s.id = :studyRoomId and s.user.id = :userId and s.deletedAt is null")
    Optional<StudyRoom> findByIdAndUserId(@Param("studyRoomId") Long studyRoomId, @Param("userId") UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StudyRoom s where s.id = :studyRoomId and s.deletedAt is null")
    Optional<StudyRoom> findByIdWithLock(@Param("studyRoomId") Long studyRoomId);
}
