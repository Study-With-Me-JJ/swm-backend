package com.jj.swm.domain.study.recruitmentposition.repository;

import com.jj.swm.domain.study.recruitmentposition.dto.AcceptedStudyParticipationCountInfo;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyParticipationRepository extends JpaRepository<StudyParticipation, Long> {

    @Query("select count(*) from StudyParticipation p where p.recruitmentPosition.id = ?1 and p.status = 'ACCEPTED'")
    int countByRecruitmentPositionIdAndAcceptedStatus(Long recruitmentPositionId);

    @Query("""
            select p.recruitmentPosition.id as recruitmentPositionId, count(*) as acceptedStudyParticipationCount
            from StudyParticipation p
            where p.recruitmentPosition.id in ?1 and p.status = 'ACCEPTED'
            group by p.recruitmentPosition.id
            """
    )
    List<AcceptedStudyParticipationCountInfo> countByRecruitmentPositionIdsAndAcceptedStatus(
            List<Long> recruitmentPositionIds
    );
}
