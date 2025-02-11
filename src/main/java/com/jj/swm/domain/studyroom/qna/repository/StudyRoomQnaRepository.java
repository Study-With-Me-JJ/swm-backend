package com.jj.swm.domain.studyroom.qna.repository;

import com.jj.swm.domain.studyroom.qna.entity.StudyRoomQna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            "where s.id = ?1")
    Optional<StudyRoomQna> findByIdWithParent(Long studyRoomQnaId, UUID userId);

    Optional<StudyRoomQna> findByIdAndUserId(Long studyRoomQnaId, UUID userId);

    @Modifying
    @Query("update StudyRoomQna s " +
            "set s.deletedAt = CURRENT_TIMESTAMP " +
            "where (s.id = ?1 and s.user.id = ?2) " +
            "or s.parent.id = ?1")
    void deleteAllByIdOrParentIdAndUserId(Long studyRoomQnaId, UUID userId);

    @Query("select s from StudyRoomQna s join fetch s.user where s.studyRoom.id = ?1 and s.parent is null")
    Page<StudyRoomQna> findPagedQnaWithUserByStudyRoomId(Long studyRoomId, Pageable pageable);

    @Modifying
    @Query("update StudyRoomQna s set s.deletedAt = CURRENT_TIMESTAMP where s.studyRoom.id = ?1")
    void deleteAllByStudyRoomId(Long studyRoomId);

    // Test 코드를 위한 JPQL
    @Modifying
    @Query(value = "DELETE FROM study_room_qna", nativeQuery = true)
    void deleteAllByIdOrParentId(@Param("studyRoomQnaId") Long studyRoomQnaId);
}
