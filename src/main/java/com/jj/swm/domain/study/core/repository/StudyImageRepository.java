package com.jj.swm.domain.study.core.repository;

import com.jj.swm.domain.study.core.entity.StudyImage;
import com.jj.swm.domain.study.core.repository.jdbc.JdbcStudyImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyImageRepository extends JpaRepository<StudyImage, Long>, JdbcStudyImageRepository {

    @Modifying
    @Query("delete from StudyImage i where i.id in ?1 and i.study.id = ?2")
    void deleteAllByIdListAndStudyId(List<Long> idList, Long studyId);

    List<StudyImage> findAllByStudyId(Long studyId);

    @Modifying
    @Query("delete from StudyImage i where i.study.id = ?1")
    void deleteAllByStudyId(Long studyId);
}
