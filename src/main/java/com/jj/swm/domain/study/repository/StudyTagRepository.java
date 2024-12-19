package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyTagRepository extends JpaRepository<StudyTag, Long>, JdbcStudyTagRepository {

    @Modifying
    @Query("update StudyTag st set st.deletedAt = CURRENT_TIMESTAMP where st.id in ?1 and st.study.id = ?2")
    void deleteAllByIdInAndStudyId(List<Long> ids, Long studyId);

    @Modifying
    @Query("update StudyTag st set st.deletedAt = CURRENT_TIMESTAMP where st.study.id = ?1")
    void deleteAllByStudyId(Long studyId);
}
