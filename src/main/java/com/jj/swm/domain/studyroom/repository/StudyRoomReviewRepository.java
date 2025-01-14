package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudyRoomReviewRepository extends JpaRepository<StudyRoomReview, Long> {

    int countStudyRoomReviewByStudyRoom(StudyRoom studyRoom);

    Optional<StudyRoomReview> findByIdAndUserId(Long studyRoomReviewId, UUID userId);

    List<StudyRoomReview> findByStudyRoomId(Long studyRoomId);

    @Query(value = "select srr.* " +
            "from study_room_review srr " +
            "inner join study_room sr on srr.study_room_id = sr.id " +
            "inner join users u on sr.user_id = u.id " +
            "where srr.id = :studyRoomReviewId and (srr.user_id = :userId or u.id = :userId) " +
            "and srr.deleted_at is null", nativeQuery = true)
    Optional<StudyRoomReview> findByStudyRoomReviewWithNativeQuery(
            @Param("studyRoomReviewId") Long studyRoomReviewId,
            @Param("userId") UUID userId);

    @Query("select s from StudyRoomReview s join fetch s.user where s.studyRoom.id = ?1")
    Page<StudyRoomReview> findPagedReviewWithUserByStudyRoomId(Long studyRoomId, Pageable pageable);

    @Query("""
                select s from StudyRoomReview s join fetch s.user
                where s.studyRoom.id = ?1 and
                exists (select 1 from StudyRoomReviewImage sri where sri.studyRoomReview.id = s.id)
           """)
    Page<StudyRoomReview> findPagedReviewWithOnlyImageAndUserByStudyRoomId(Long studyRoomId, Pageable pageable);

    @Modifying
    @Query("update StudyRoomReview s set s.deletedAt = CURRENT_TIMESTAMP where s.id in (?1)")
    void deleteAllByReviewIds(List<Long> reviewIds);
}
