package com.jj.swm.domain.study.recruitmentposition.repository.jdbc;

import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;

import java.util.List;

public interface JdbcStudyParticipationLinkRepository {

    void batchInsert(List<String> links, StudyParticipation participation);

}
