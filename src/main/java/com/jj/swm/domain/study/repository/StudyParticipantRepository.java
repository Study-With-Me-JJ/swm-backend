//package com.jj.swm.domain.study.repository;
//
//import com.jj.swm.domain.study.entity.StudyParticipant;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface StudyParticipantRepository extends JpaRepository<StudyParticipant, Long> {
//
//    Optional<StudyParticipant> findByUserIdAndStudyRecruitmentPositionId(UUID userId, Long recruitmentPositionId);
//
//    Optional<StudyParticipant> findByIdAndStudyUserId(Long id, UUID userId);
//
//    @Query("select count(p) from StudyParticipant p where p.status = 'ACCEPTED' and p.studyRecruitmentPosition = ?1")
//    Integer countAcceptedByRecruitmentPositionId(StudyRecruitmentPosition recruitmentPosition);
//}
