package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoomQna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudyRoomQnaRepository extends JpaRepository<StudyRoomQna, Long> {

    @Query("select s from StudyRoomQna s " +
            "left join fetch s.parent p " +
            "where s.id = ?1 and s.deletedAt is null")
    Optional<StudyRoomQna> findByIdWithParent(Long studyRoomQnaId, UUID userId);

    Optional<StudyRoomQna> findByIdAndUserId(Long studyRoomQnaId, UUID userId);

    @Modifying
    @Query("update StudyRoomQna s " +
            "set s.deletedAt = CURRENT_TIMESTAMP " +
            "where (s.id = ?1 and s.user.id = ?2) " +
            "or s.parent.id = ?1")
    void deleteAllByIdOrParentIdAndUserId(Long studyRoomQnaId, UUID userId);

    // Test 코드를 위한 JPQL
    @Modifying
    @Query(value = "DELETE FROM study_room_qna " +
            "WHERE (parent_id = :studyRoomQnaId OR id = :studyRoomQnaId) " +
            "AND deleted_at IS NOT NULL", nativeQuery = true)
    void deleteAllByIdOrParentId(@Param("studyRoomQnaId") Long studyRoomQnaId);
}
