package com.jj.swm.domain.studyroom.core.repository;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.repository.custom.CustomStudyRoomRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long>, CustomStudyRoomRepository {

    boolean existsByIdAndUserId(Long studyRoomId, UUID userId);

    @Query("select s.id from StudyRoom s where s.user.id = ?1")
    List<Long> findStudyRoomIdsByUserId(UUID userId);

    @Query("select s from StudyRoom s join fetch s.user " +
            "where s.id = :studyRoomId and s.user.id = :userId")
    Optional<StudyRoom> findByIdAndUserIdWithUser(@Param("studyRoomId") Long studyRoomId, @Param("userId") UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StudyRoom s where s.id = :studyRoomId")
    Optional<StudyRoom> findByIdWithLock(@Param("studyRoomId") Long studyRoomId);

    @Query("select s from StudyRoom s join fetch s.user " +
            "where s.id = ?1")
    Optional<StudyRoom> findByIdWithUser(Long studyRoomId);

    @Query("select distinct(s) from StudyRoom s left join fetch s.tags where s.id = ?1")
    Optional<StudyRoom> findByIdWithTags(Long studyRoomId);

    @Modifying
    @Query("update StudyRoom s set s.deletedAt = CURRENT_TIMESTAMP where s.id = ?1")
    void deleteByIdWithJpql(Long studyRoomId);
}
