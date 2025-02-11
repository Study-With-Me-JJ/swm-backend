package com.jj.swm.domain.studyroom.core.repository;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomReserveType;
import com.jj.swm.domain.studyroom.core.repository.jdbc.JdbcReserveTypeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRoomReserveTypeRepository extends
        JpaRepository<StudyRoomReserveType, Long>, JdbcReserveTypeRepository {

    List<StudyRoomReserveType> findAllByIdInAndStudyRoom(List<Long> reserveTypeIds, StudyRoom studyRoom);

    int countStudyRoomReserveTypeByIdInAndStudyRoom(List<Long> reserveTypeIds, StudyRoom studyRoom);

    List<StudyRoomReserveType> findAllByStudyRoomId(Long studyRoomId);

    @Modifying
    @Query("update StudyRoomReserveType s set s.deletedAt = CURRENT_TIMESTAMP where s.id in :reserveTypeIds")
    void deleteAllByIdInBatch(@Param("reserveTypeIds") List<Long> reserveTypeIds);

    @Modifying
    @Query("update StudyRoomReserveType s set s.deletedAt = CURRENT_TIMESTAMP where s.studyRoom.id = ?1")
    void deleteAllByStudyRoomId(Long studyRoomId);
}
