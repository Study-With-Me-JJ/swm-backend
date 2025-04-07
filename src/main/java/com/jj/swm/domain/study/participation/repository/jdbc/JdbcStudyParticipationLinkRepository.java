package com.jj.swm.domain.study.participation.repository.jdbc;

import com.jj.swm.domain.study.participation.entity.StudyParticipation;

import java.util.List;

public interface JdbcStudyParticipationLinkRepository {

    void batchInsert(List<String> links, StudyParticipation participation);

}
