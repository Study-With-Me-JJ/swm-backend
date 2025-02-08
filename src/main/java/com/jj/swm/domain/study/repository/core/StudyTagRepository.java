package com.jj.swm.domain.study.repository.core;

import com.jj.swm.domain.study.entity.core.StudyTag;
import com.jj.swm.domain.study.repository.jdbc.JdbcStudyTagRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyTagRepository extends JpaRepository<StudyTag, Long>, JdbcStudyTagRepository {

    @Modifying
    @Query("update StudyTag t set t.deletedAt = CURRENT_TIMESTAMP where t.id in ?1 and t.study.id = ?2")
    void deleteAllByIdInAndStudyId(List<Long> ids, Long studyId);

    @Modifying
    @Query("update StudyTag t set t.deletedAt = CURRENT_TIMESTAMP where t.study.id = ?1")
    void deleteAllByStudyId(Long studyId);
}
