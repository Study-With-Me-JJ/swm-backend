package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomOptionInfo;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcOptionInfoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRoomOptionInfoRepository extends
        JpaRepository<StudyRoomOptionInfo, Long>, JdbcOptionInfoRepository {

    List<StudyRoomOptionInfo> findAllByIdInAndStudyRoom(List<Long> optionIds, StudyRoom studyRoom);

    int countStudyRoomOptionInfoByIdInAndStudyRoom(List<Long> optionIds, StudyRoom studyRoom);
}
