package com.jj.swm.domain.studyroom.repository;

import com.jj.swm.domain.studyroom.entity.StudyRoomReserveType;
import com.jj.swm.domain.studyroom.repository.jdbc.JdbcReserveTypeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRoomReserveTypeRepository extends
        JpaRepository<StudyRoomReserveType, Long>, JdbcReserveTypeRepository {
}
