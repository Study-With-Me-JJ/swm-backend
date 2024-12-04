package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.StudyTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyTagRepository extends JpaRepository<StudyTag, Long>, JdbcStudyTagRepository {
}
