package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoomTag;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcTagRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRoomTagRepository extends JpaRepository<StudyRoomTag, Long>, JdbcTagRepository {
}
