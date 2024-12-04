package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyImageRepository extends JpaRepository<StudyImage, Long>, JdbcStudyImageRepository {
}
