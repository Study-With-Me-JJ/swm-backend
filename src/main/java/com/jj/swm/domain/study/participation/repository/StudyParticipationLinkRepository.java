package com.jj.swm.domain.study.participation.repository;

import com.jj.swm.domain.study.participation.entity.StudyParticipationLink;
import com.jj.swm.domain.study.participation.repository.jdbc.JdbcStudyParticipationLinkRepository;
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

    @Modifying
    @Query("delete from StudyParticipationLink pl where pl.participation.id = ?1")
    void deleteAllByParticipationId(Long participationId);

    List<StudyParticipationLink> findAllByParticipationId(Long participationId);

    @Modifying
    @Query("delete from StudyParticipationLink pl where pl.participation.id in (?1)")
    void deleteAllByParticipationIds(List<Long> participationIds);
}
