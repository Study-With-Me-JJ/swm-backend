package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyImageRepository extends JpaRepository<StudyImage, Long>, JdbcStudyImageRepository {

    @Modifying
    @Query("delete from StudyImage si where si.id in ?1 and si.study.id = ?2")
    void deleteAllByIdInAndStudyId(List<Long> deleteImageIds, Long studyId);
}
