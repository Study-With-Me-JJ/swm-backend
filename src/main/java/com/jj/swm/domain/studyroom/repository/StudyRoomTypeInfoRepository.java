package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomTypeInfo;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcTypeInfoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface StudyRoomTypeInfoRepository extends JpaRepository<StudyRoomTypeInfo, Long>, JdbcTypeInfoRepository {

    List<StudyRoomTypeInfo> findAllByIdInAndStudyRoom(List<Long> typeIds, StudyRoom studyRoom);

    int countStudyRoomTypeInfoByIdInAndStudyRoom(List<Long> optionIds, StudyRoom studyRoom);

    List<StudyRoomTypeInfo> findAllByStudyRoomId(Long studyRoomId);

    @Modifying
    @Query("delete from StudyRoomTypeInfo s where s.studyRoom.id = ?1")
    void deleteAllByStudyRoomId(Long studyRoomId);
}
