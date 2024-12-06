package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomReserveType;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcReserveTypeRepository;
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

    @Modifying
    @Query("update StudyRoomReserveType s set s.deletedAt = CURRENT_TIMESTAMP where s.id in :reserveTypeIds")
    void deleteAllByIdInBatch(@Param("reserveTypeIds") List<Long> reserveTypeIds);

}
