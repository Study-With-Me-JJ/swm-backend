package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoomDayOff;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcDayOffRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRoomDayOffRepository extends JpaRepository<StudyRoomDayOff, Long>, JdbcDayOffRepository {
}
