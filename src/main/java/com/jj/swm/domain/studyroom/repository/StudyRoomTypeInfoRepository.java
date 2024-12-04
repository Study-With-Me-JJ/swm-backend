package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoomTypeInfo;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcTypeInfoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRoomTypeInfoRepository extends JpaRepository<StudyRoomTypeInfo, Long>, JdbcTypeInfoRepository {
}
