package com.jj.swm.domain.studyroom.reservation.repository;

import com.jj.swm.domain.studyroom.reservation.entity.StudyRoomReservationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudyRoomReservationInfoRepository extends JpaRepository<StudyRoomReservationInfo, Long> {

    @Query("select s from StudyRoomReservationInfo s left join fetch s.studyRoom where s.id = ?1")
    Optional<StudyRoomReservationInfo> findByIdWithStudyRoom(Long studyRoomReservationInfoId);
}
