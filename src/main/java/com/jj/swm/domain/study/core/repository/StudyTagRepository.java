package com.jj.swm.domain.study.core.repository;

import com.jj.swm.domain.study.core.entity.StudyTag;
import com.jj.swm.domain.study.core.repository.jdbc.JdbcStudyTagRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyTagRepository extends JpaRepository<StudyTag, Long>, JdbcStudyTagRepository {

    List<StudyTag> findAllByStudyId(Long studyId);

    @Modifying
    @Query("update StudyTag t set t.deletedAt = CURRENT_TIMESTAMP where t.id in ?1")
    void deleteAllByIds(List<Long> ids);

    @Modifying
    @Query("update StudyTag t set t.deletedAt = CURRENT_TIMESTAMP where t.study.id in ?1")
    void deleteAllByStudyIds(List<Long> studyIds);
}
