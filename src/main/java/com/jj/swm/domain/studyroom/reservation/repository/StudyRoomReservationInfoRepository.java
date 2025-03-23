package com.jj.swm.domain.studyroom.reservation.repository;

import com.jj.swm.domain.studyroom.reservation.entity.StudyRoomReservationInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRoomReservationInfoRepository extends JpaRepository<StudyRoomReservationInfo, Long> {
}
