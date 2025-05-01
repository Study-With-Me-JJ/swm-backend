package com.jj.swm.domain.studyroom.reservation.repository.custom;

import com.jj.swm.domain.studyroom.reservation.dto.response.GetOwnerReservationInfoResponse;
import com.jj.swm.domain.studyroom.reservation.dto.response.GetReserverReservationInfoResponse;
import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CustomReservationInfoRepository {

    Page<GetOwnerReservationInfoResponse> findPagedReservationInfoByStudyRoomIdsAndStatus(List<Long> studyRoomIds, ApprovalStatus status, Pageable pageable);

    Page<GetReserverReservationInfoResponse> findPagedReservationInfoByUserIdAndStatus(UUID userId, ApprovalStatus status, Pageable pageable);
}
