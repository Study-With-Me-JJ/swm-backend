package com.jj.swm.domain.study.recruitmentposition.repository.jdbc;

import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;

import java.util.List;

public interface JdbcStudyParticipationAttachmentRepository {

    void batchInsert(List<String> fileUrls, StudyParticipation participation);
}
