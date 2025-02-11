package com.jj.swm.domain.studyroom.core.repository;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomOptionInfo;
import com.jj.swm.domain.studyroom.core.repository.jdbc.JdbcOptionInfoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRoomOptionInfoRepository extends
        JpaRepository<StudyRoomOptionInfo, Long>, JdbcOptionInfoRepository {

    int countStudyRoomOptionInfoByIdInAndStudyRoom(List<Long> optionIds, StudyRoom studyRoom);

    List<StudyRoomOptionInfo> findAllByStudyRoomId(Long studyRoomId);

    @Modifying
    @Query("delete from StudyRoomOptionInfo s where s.studyRoom.id = ?1")
    void deleteAllByStudyRoomId(Long studyRoomId);

    boolean existsByStudyRoomIdAndOptionIn(Long studyRoomId, List<StudyRoomOption> options);
}
