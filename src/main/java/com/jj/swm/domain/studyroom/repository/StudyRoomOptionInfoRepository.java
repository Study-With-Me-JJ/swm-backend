package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.entity.StudyRoomOptionInfo;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcOptionInfoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRoomOptionInfoRepository extends
        JpaRepository<StudyRoomOptionInfo, Long>, JdbcOptionInfoRepository {
}
