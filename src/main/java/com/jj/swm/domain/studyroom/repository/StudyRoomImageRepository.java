package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoomImage;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRoomImageRepository extends JpaRepository<StudyRoomImage, Long>, JdbcImageRepository {
}
