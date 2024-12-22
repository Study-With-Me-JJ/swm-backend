package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomDayOff;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcDayOffRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRoomDayOffRepository extends JpaRepository<StudyRoomDayOff, Long>, JdbcDayOffRepository {

    int countStudyRoomDayOffByIdInAndStudyRoom(List<Long> dayOffIds, StudyRoom studyRoom);

    List<StudyRoomDayOff> findAllByStudyRoomId(Long studyRoomId);

    @Modifying
    @Query("delete from StudyRoomDayOff s where s.studyRoom.id = ?1")
    void deleteAllByStudyRoomId(Long studyRoomId);
}
