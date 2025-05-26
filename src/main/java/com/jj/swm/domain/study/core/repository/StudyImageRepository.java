package com.jj.swm.domain.study.core.repository;

import com.jj.swm.domain.study.core.entity.StudyImage;
import com.jj.swm.domain.study.core.repository.jdbc.JdbcStudyImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyImageRepository extends JpaRepository<StudyImage, Long>, JdbcStudyImageRepository {

    List<StudyImage> findAllByStudyId(Long studyId);

    long countByStudyId(Long studyId);

    @Modifying
    @Query("delete from StudyImage i where i.id in ?1")
    void deleteAllByIds(List<Long> ids);

    @Modifying
    @Query("delete from StudyImage i where i.study.id in ?1")
    void deleteAllByStudyIds(List<Long> studyIds);
}
