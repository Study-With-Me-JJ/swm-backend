package com.jj.swm.domain.study.recruitmentposition.repository;

import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipationLink;
import com.jj.swm.domain.study.recruitmentposition.repository.jdbc.JdbcStudyParticipationLinkRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyParticipationLinkRepository extends
        JpaRepository<StudyParticipationLink, Long>, JdbcStudyParticipationLinkRepository {

    int countByParticipationId(Long participationId);

    @Modifying
    @Query("delete from StudyParticipationLink pl where pl.id in ?1 and pl.participation.id = ?2")
    void deleteAllByIdsAndParticipationId(List<Long> ids, Long participationId);
}
