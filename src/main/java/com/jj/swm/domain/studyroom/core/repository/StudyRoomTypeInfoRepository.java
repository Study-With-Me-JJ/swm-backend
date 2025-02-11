package com.jj.swm.domain.studyroom.core.repository;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomType;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomTypeInfo;
import com.jj.swm.domain.studyroom.core.repository.jdbc.JdbcTypeInfoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRoomTypeInfoRepository extends JpaRepository<StudyRoomTypeInfo, Long>, JdbcTypeInfoRepository {

    int countStudyRoomTypeInfoByIdInAndStudyRoom(List<Long> optionIds, StudyRoom studyRoom);

    List<StudyRoomTypeInfo> findAllByStudyRoomId(Long studyRoomId);

    @Modifying
    @Query("delete from StudyRoomTypeInfo s where s.studyRoom.id = ?1")
    void deleteAllByStudyRoomId(Long studyRoomId);

    long countByStudyRoomId(Long studyRoomId);

    boolean existsByStudyRoomIdAndTypeIn(Long studyRoomId, List<StudyRoomType> typesToAdd);
}
