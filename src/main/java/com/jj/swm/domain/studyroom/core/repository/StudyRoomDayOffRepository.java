package com.jj.swm.domain.studyroom.core.repository;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomDayOff;
import com.jj.swm.domain.studyroom.core.repository.jdbc.JdbcDayOffRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface StudyRoomDayOffRepository extends JpaRepository<StudyRoomDayOff, Long>, JdbcDayOffRepository {

    int countStudyRoomDayOffByIdInAndStudyRoom(List<Long> dayOffIds, StudyRoom studyRoom);

    List<StudyRoomDayOff> findAllByStudyRoomId(Long studyRoomId);

    @Modifying
    @Query("delete from StudyRoomDayOff s where s.studyRoom.id in ?1")
    void deleteByStudyRoomIds(List<Long> studyRoomId);

    long countByStudyRoomId(Long studyRoomId);

    boolean existsByStudyRoomIdAndDayOfWeekIn(Long studyRoomId, List<DayOfWeek> dayOfWeeks);
}
