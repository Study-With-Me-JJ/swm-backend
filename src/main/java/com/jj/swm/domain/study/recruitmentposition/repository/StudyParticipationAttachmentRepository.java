package com.jj.swm.domain.study.recruitmentposition.repository;

import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipationAttachment;
import com.jj.swm.domain.study.recruitmentposition.repository.jdbc.JdbcStudyParticipationAttachmentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyParticipationAttachmentRepository extends
        JpaRepository<StudyParticipationAttachment, Long>, JdbcStudyParticipationAttachmentRepository {
}
